package com.vicarius.ratelimiter.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.dto.UserQuotaDto;
import com.vicarius.ratelimiter.mapper.UserMapper;
import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import com.vicarius.ratelimiter.service.UserMysqlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.profiles.active=test"})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private MockMvc mvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BlockingFilter blockingFilter;

    @Autowired
    private WebApplicationContext wac;

    private static Integer registeringCounter = 0;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        this.mvc = webAppContextSetup(this.wac)
                .addFilters(blockingFilter).build();
        if (registeringCounter == 0) {
            setupClock();
        }
    }

    @Test
    public void givenCreatedUserWhenCallingManyTimesThenIsBlocked() throws Exception {
        registeringCounter++;
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult mvcResult = createUser(objectMapper);

        final UserDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), UserDto.class);

        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        List<MvcResult> resultList = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    setupClock();
                    MvcResult loopResult = mvc.perform(get("/user/" + result.getId().toString()))
                            .andReturn();
                    resultList.add(loopResult);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });
        }
        latch.await();

        userRepository.deleteAll();
        UserMysqlServiceImpl.deleteQuotaLimiter(result.getId());

        int blockedCounter = 0;

        for (MvcResult retrievedResult : resultList) {
            if (retrievedResult.getResponse().getStatus() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                blockedCounter++;
            }
        }

        assertThat(blockedCounter).isEqualTo(5);
    }

    @Test
    public void givenCreatedManyUsersAndBlockedWhenGetALlUsersQuotaThenRetrievedProperly() throws Exception {
        registeringCounter++;
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult mvcResult = createUser(objectMapper);
        createUser(objectMapper);
        createUser(objectMapper);
        createUser(objectMapper);
        createUser(objectMapper);
        createUser(objectMapper);

        final UserDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), UserDto.class);

        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        List<MvcResult> resultList = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    setupClock();
                    MvcResult loopResult = mvc.perform(get("/user/" + result.getId().toString()))
                            .andReturn();
                    resultList.add(loopResult);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });
        }
        latch.await();

        MvcResult quotaResult = mvc.perform(get("/user/quotas"))
                .andReturn();

        List<UserQuotaDto> userQuotaDtoList = objectMapper.readValue(
                quotaResult.getResponse().getContentAsString(), new TypeReference<>() {
                });
        for (UserQuotaDto userQuotaDto : userQuotaDtoList) {
            if (userQuotaDto.getId().equals(result.getId())) {
                assertThat(userQuotaDto.getQuotaNumber()).isEqualTo(0);
            }
        }
        assertThat(userQuotaDtoList).hasSize(6);
    }

    private MvcResult createUser(ObjectMapper objectMapper) throws Exception {
        User user = User.builder().firstName("Test first Name").lastName("Test last name").disabled(false).build();
        UserDto userDto = userMapper.toUserDto(user);

        MvcResult mvcResult = mvc.perform(post("/user")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        return mvcResult;
    }


    private void setupClock() {
        Instant now = Instant.now();

        Instant newInstant = now.atZone(ZoneOffset.UTC)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();

        var clock = Clock.fixed(newInstant, ZoneOffset.UTC);
        var mockedInstant = Instant.now(clock);
        MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS);
        mockedStatic.when(Instant::now).thenReturn(mockedInstant).thenReturn(mockedInstant).thenReturn(mockedInstant)
                .thenReturn(mockedInstant).thenReturn(mockedInstant).thenReturn(mockedInstant).thenReturn(mockedInstant)
                .thenReturn(mockedInstant).thenReturn(mockedInstant).thenReturn(mockedInstant);
    }

}