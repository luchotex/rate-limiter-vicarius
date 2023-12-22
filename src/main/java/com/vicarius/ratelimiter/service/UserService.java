package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;

import java.util.UUID;

public interface UserService {

    UserDto getById(UUID id);

    UserDto save(UserDto userDto);

    UserDto update(UUID id, UserDto userDto);

    UserDto delete(UUID id);
}
