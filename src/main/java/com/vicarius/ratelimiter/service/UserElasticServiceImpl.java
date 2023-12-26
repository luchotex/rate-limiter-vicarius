package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.dto.UserQuotaDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class UserElasticServiceImpl implements UserService {

    public static final String NAME ="UserElasticServiceImpl";


    @Override
    public UserDto getById(UUID id) {
        log.info("getting into get by id");

        return null;
    }

    @Override
    public UserDto save(UserDto userDto) {
        log.info("getting into save");
        return null;
    }

    @Override
    public UserDto update(UUID id, UserDto userDto) {
        log.info("getting into update");
        return null;
    }

    @Override
    public UserDto delete(UUID id) {
        log.info("getting into delete");
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public UserQuotaDto consumeQuota(UUID id) {
        log.info("getting into consume quota");
        return null;
    }

    @Override
    public List<UserQuotaDto> getUsersQuota() {
        log.info("getting into get users quota");
        return new ArrayList<>();
    }
}
