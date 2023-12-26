package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.dto.UserQuotaDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomMapDynamicAutowireService {
    private final Map<String, UserService> serviceByName;

    public CustomMapDynamicAutowireService(List<UserService> userServiceList) {
        serviceByName = userServiceList.stream().collect(Collectors.toMap(UserService::getName, Function.identity()));
    }

    public UserDto getById(UUID id) {
        UserService userService = getService();

        return userService.getById(id);

    }

    public UserDto save(UserDto userDto) {
        UserService userService = getService();

        return userService.save(userDto);
    }

    public UserDto update(UUID id, UserDto userDto) {
        UserService userService = getService();

        return userService.update(id, userDto);
    }

    public UserDto delete(UUID id) {
        UserService userService = getService();

        return userService.delete(id);
    }

    public UserQuotaDto consumeQuota(UUID id) {
        UserService userService = getService();

        return userService.consumeQuota(id);

    }

    public List<UserQuotaDto> getUsersQuota() {
        UserService userService = getService();

        return userService.getUsersQuota();

    }

    private UserService getService() {

        Instant now = Instant.now();
        Instant lowerLimit = now.atZone(ZoneOffset.UTC)
                .withHour(9)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();
        Instant upperLimit = now.atZone(ZoneOffset.UTC)
                .withHour(17)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();

        UserService userService = serviceByName.get(UserElasticServiceImpl.NAME);

        if ((now.isAfter(lowerLimit) && now.isBefore(upperLimit)) || now.equals(lowerLimit) || now.equals(upperLimit)) {
            userService = serviceByName.get(UserMysqlServiceImpl.NAME);
        }
        return userService;
    }
}
