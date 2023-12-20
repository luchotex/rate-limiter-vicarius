package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.exception.UserException;
import com.vicarius.ratelimiter.mapper.UserMapper;
import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserMysqlServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto update(String id, UserDto userDto) {
        return null;
    }

    @Override
    public UserDto delete(String id) {
        return null;
    }
}
