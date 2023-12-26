package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    private static Map<String, Integer> counterMap = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting user counter map loading");
        List<User> allNotDisabled = userRepository.findAllByDisabled(false);

        for (User user : allNotDisabled) {
            counterMap.put(user.getId().toString(), user.getQuotaNumber());
        }

        log.info("Finished user counter map loading");
    }
}
