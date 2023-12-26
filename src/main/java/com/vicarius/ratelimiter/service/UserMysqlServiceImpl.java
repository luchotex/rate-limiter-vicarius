package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.exception.UserNotException;
import com.vicarius.ratelimiter.mapper.UserMapper;
import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import com.vicarius.ratelimiter.service.limiter.QuotaLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;


@RequiredArgsConstructor
@Service
public class UserMysqlServiceImpl implements UserService {

    @Value("${vicarius.max-quota}")
    private Integer maxQuota;
    public static final String NAME = "UserMysqlServiceImpl";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(UUID id) {
        User user = userRepository.findByIdAndDisabled(id, false).orElseThrow(() -> new UserNotException("User not found"));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User userToSave = userMapper.toUser(userDto);
        userToSave.setQuotaNumber(maxQuota);
        User userSaved = userRepository.save(userToSave);
        UserLoader.counterMap.put(userSaved.getId().toString(),
                QuotaLimiter.builder().quotaNumber(maxQuota).reentrantLock(new ReentrantLock()).build());

        return userMapper.toUserDto(userSaved);
    }

    @Override
    public UserDto update(UUID id, UserDto userDto) {
        User userToUpdate = userRepository.findByIdAndDisabled(id, false).orElseThrow(() -> new UserNotException("User not found"));
        userToUpdate = userMapper.toUser(userDto);
        userRepository.save(userToUpdate);

        return userMapper.toUserDto(userToUpdate);
    }

    @Override
    public UserDto delete(UUID id) {
        User userToDelete = userRepository.findById(id).orElseThrow(() -> new UserNotException("User not Found"));
        userToDelete.setDisabled(true);
        userRepository.save(userToDelete);

        return userMapper.toUserDto(userToDelete);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
