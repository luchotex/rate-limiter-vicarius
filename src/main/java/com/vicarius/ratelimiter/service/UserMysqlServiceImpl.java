package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.dto.UserQuotaDto;
import com.vicarius.ratelimiter.exception.UserNotFoundException;
import com.vicarius.ratelimiter.mapper.UserMapper;
import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import com.vicarius.ratelimiter.service.limiter.QuotaLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserMysqlServiceImpl implements UserService {
    public static final String NAME = "UserMysqlServiceImpl";

    @Value("${vicarius.max-quota}")
    private Integer maxQuota;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(UUID id) {
        User user = userRepository.findByIdAndDisabled(id, false).orElseThrow(() -> new UserNotFoundException("User not found"));
        log.info("Retrieved userId {}", id.toString());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User userToSave = userMapper.toUser(userDto);
        userToSave.setQuotaNumber(maxQuota);
        User userSaved = userRepository.save(userToSave);
        log.info("Saved userId {} with quota number {}", userSaved.getId(), userSaved.getQuotaNumber());
        UserLoader.counterMap.put(userSaved.getId().toString(),
                QuotaLimiter.builder().quotaNumber(maxQuota).reentrantLock(new ReentrantLock()).build());

        return userMapper.toUserDto(userSaved);
    }

    @Override
    public UserDto update(UUID id, UserDto userDto) {
        User userToUpdate = userRepository.findByIdAndDisabled(id, false).orElseThrow(() -> new UserNotFoundException("User not found"));
        userToUpdate = userMapper.toUser(userDto);
        User userUpdated = userRepository.save(userToUpdate);

        log.info("Updated userId {} with quota number {}", userUpdated.getId(), userUpdated.getQuotaNumber());

        return userMapper.toUserDto(userToUpdate);
    }

    @Override
    public UserDto delete(UUID id) {
        User userToDelete = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not Found"));
        userToDelete.setDisabled(true);
        userRepository.save(userToDelete);

        log.info("Deleted userId {}", userToDelete.getId());

        return userMapper.toUserDto(userToDelete);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public UserQuotaDto consumeQuota(UUID id) {

        QuotaLimiter quotaLimiter = UserLoader.counterMap.get(id.toString());
        if (quotaLimiter == null) {
            throw new UserNotFoundException("Can't find user");
        }
        ReentrantLock reentrantLock = quotaLimiter.getReentrantLock();
        UserQuotaDto userQuotaDto;

        try {
            reentrantLock.lock();
            User user = userRepository.findByIdAndDisabled(id, false).orElseThrow();
            user.setLastLoginUtc(LocalDateTime.now(ZoneOffset.UTC));
            userRepository.save(user);
            userQuotaDto = userMapper.toUserQuotaDto(user);
            userQuotaDto.setQuotaNumber(quotaLimiter.getQuotaNumber());

        } finally {
            reentrantLock.unlock();
        }
        return userQuotaDto;
    }

    @Override
    public List<UserQuotaDto> getUsersQuota() {
        ReentrantLock reentrantLock = UserLoader.reentrantLock;
        List<UserQuotaDto> userQuotaDtoList = new ArrayList<>();

        try {
            reentrantLock.lock();
            Set<Map.Entry<String, QuotaLimiter>> entries = UserLoader.counterMap.entrySet();
            for (Map.Entry<String, QuotaLimiter> entry: entries) {
                String userId = entry.getKey();
                QuotaLimiter quotaLimiter = entry.getValue();
                User user = userRepository.findByIdAndDisabled(UUID.fromString(userId), false).orElseThrow();
                UserQuotaDto userQuotaDto = userMapper.toUserQuotaDto(user);
                userQuotaDto.setQuotaNumber(quotaLimiter.getQuotaNumber());
                userQuotaDtoList.add(userQuotaDto);
            }

        } finally {
            reentrantLock.unlock();
        }
        return userQuotaDtoList;
    }


}
