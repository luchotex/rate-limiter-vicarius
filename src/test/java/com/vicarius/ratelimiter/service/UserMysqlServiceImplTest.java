package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.mapper.UserMapper;
import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMysqlServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserMysqlServiceImpl service;


    @Test
    void givenNoUserCallingGetByIdThenExceptionThrown() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        when(userRepository.findByIdAndDisabled(any(), anyBoolean())).thenReturn(Optional.empty());
        try {
            service.getById(id);
            assert (false);
        } catch (Exception exception) {
            assert (true);
            // Then
            verify(userRepository).findByIdAndDisabled(any(), anyBoolean());
            assertThat(exception.getMessage()).isEqualTo("User not found");
        }
    }

    @Test
    void givenAllInfoWhenCallingGetByIdThenWasCorrectlyCalled() {
        // Given
        User user = User.builder().firstName("Test first Name").lastName("Test last name").disabled(false).build();
        UUID id = UUID.randomUUID();

        // When
        when(userRepository.findByIdAndDisabled(any(), anyBoolean())).thenReturn(Optional.of(user));
        UserDto response = service.getById(id);

        // Then
        assertThat(response).isNotNull();
        verify(userRepository).findByIdAndDisabled(any(), anyBoolean());
    }

    @Test
    void givenAllInfoWhenCallSaveThenEverythingWasCorrectlySet() {

        // Given
        User userSaved = User.builder().id(UUID.randomUUID())
                .firstName("Test first Name").lastName("Test last name").disabled(false).build();
        UserDto userDto = userMapper.toUserDto(userSaved);

        // When
        ReflectionTestUtils.setField(service, "maxQuota", 5);
        when(userRepository.save(any())).thenReturn(userSaved);
        UserDto response = service.save(userDto);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        assertThat(response).isNotNull();
        verify(userRepository).save(userArgumentCaptor.capture());
        User userCaptured = userArgumentCaptor.getValue();
        assertThat(userCaptured).extracting(User::getQuotaNumber, User::getFirstName).contains(5, "Test first Name");
    }

    @Test
    void givenNoUserCallingUpdateThenExceptionThrown() {
        // Given
        UUID id = UUID.randomUUID();
        User userSaved = User.builder().id(UUID.randomUUID())
                .firstName("Test first Name").lastName("Test last name").disabled(false).build();
        UserDto userDto = userMapper.toUserDto(userSaved);

        // When
        when(userRepository.findByIdAndDisabled(any(), anyBoolean())).thenReturn(Optional.empty());
        try {
            service.update(id, userDto);
            assert (false);
        } catch (Exception exception) {
            assert (true);
            // Then
            verify(userRepository).findByIdAndDisabled(any(), anyBoolean());
            verify(userRepository, times(0)).save(any());
            assertThat(exception.getMessage()).isEqualTo("User not found");
        }
    }

    @Test
    void givenAllInfoWhenCallingUpdateThenSuccessfulResponse() throws CloneNotSupportedException {
        // Given
        User userSaved = User.builder().id(UUID.randomUUID())
                .firstName("Test first Name").lastName("Test last name").disabled(false).build();
        User userToUpdate = (User) userSaved.clone();
        userToUpdate.setFirstName("Changed first name");
        UserDto userDto = userMapper.toUserDto(userToUpdate);
        UUID id = UUID.randomUUID();
        userSaved.setId(id);

        // When
        when(userRepository.findByIdAndDisabled(any(), anyBoolean())).thenReturn(Optional.of(userSaved));
        when(userRepository.save(any())).thenReturn(userSaved);
        UserDto response = service.update(id, userDto);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        assertThat(response).isNotNull();
        verify(userRepository).save(userArgumentCaptor.capture());
        User userCaptured = userArgumentCaptor.getValue();
        assertThat(userCaptured).extracting(User::getLastLoginUtc, User::getFirstName).contains(null, userToUpdate.getFirstName());
    }

    @Test
    void givenNoUserFoundWhenCallingDeleteThenThrowException() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        when(userRepository.findByIdAndDisabled(any(), anyBoolean())).thenReturn(Optional.empty());
        try {
            service.delete(id);
            assert (false);
        } catch (Exception exception) {
            assert (true);
            // Then
            verify(userRepository).findByIdAndDisabled(any(), anyBoolean());
            verify(userRepository, times(0)).save(any());
            assertThat(exception.getMessage()).isEqualTo("User not found");
        }
    }

    @Test
    void givenAllInfoWhenCallingDeleteThenSuccessfulResponse() {
        // Given
        UUID id = UUID.randomUUID();
        User userSaved = User.builder().id(UUID.randomUUID())
                .firstName("Test first Name").lastName("Test last name").disabled(false).build();

        // When
        when(userRepository.findByIdAndDisabled(any(), anyBoolean())).thenReturn(Optional.of(userSaved));
        when(userRepository.save(any())).thenReturn(userSaved);
        UserDto response = service.delete(id);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        assertThat(response).isNotNull();
        verify(userRepository).save(userArgumentCaptor.capture());
        User userCaptured = userArgumentCaptor.getValue();
        assertThat(userCaptured).extracting(User::getLastLoginUtc, User::isDisabled)
                .contains(null, true);
    }
}