package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;

public interface UserService {

    UserDto getById(String id);

    UserDto save(UserDto userDto);

    UserDto update(String id, UserDto userDto);

    UserDto delete(String id);
}
