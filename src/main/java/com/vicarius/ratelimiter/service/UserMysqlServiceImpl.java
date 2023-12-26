package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.exception.UserException;
import com.vicarius.ratelimiter.mapper.UserMapper;
import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class UserMysqlServiceImpl implements UserService {
    public static final String NAME ="UserMysqlServiceImpl";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(UUID id) {
        User user = userRepository.findByIdAndDisabled(id, false).orElseThrow(() -> new UserException("User not found"));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User userToSave = userMapper.toUser(userDto);
        User userSaved = userRepository.save(userToSave);

        return userMapper.toUserDto(userSaved);
    }

    @Override
    public UserDto update(UUID id, UserDto userDto) {
        User userToUpdate = userRepository.findByIdAndDisabled(id, false).orElseThrow(() -> new UserException("User not found"));
        userToUpdate = userMapper.toUser(userDto);
        userRepository.save(userToUpdate);

        return userMapper.toUserDto(userToUpdate);
    }

    @Override
    public UserDto delete(UUID id) {
        User userToDelete = userRepository.findById(id).orElseThrow(() -> new UserException("User not Found"));
        userToDelete.setDisabled(true);
        userRepository.save(userToDelete);

        return userMapper.toUserDto(userToDelete);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
