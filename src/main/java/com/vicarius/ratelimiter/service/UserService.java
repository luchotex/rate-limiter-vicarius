package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.dto.UserQuotaDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto getById(UUID id);

    UserDto save(UserDto userDto);

    UserDto update(UUID id, UserDto userDto);

    UserDto delete(UUID id);

    String getName();

    UserQuotaDto consumeQuota(UUID id);

    List<UserQuotaDto> getUsersQuota();
}
