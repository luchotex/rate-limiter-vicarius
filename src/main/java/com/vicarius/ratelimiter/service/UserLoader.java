package com.vicarius.ratelimiter.service;

import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import com.vicarius.ratelimiter.service.limiter.QuotaLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public static final ReentrantLock reentrantLock = new ReentrantLock();

    public static final Map<String, QuotaLimiter> counterMap = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting user counter map loading");
        List<User> allNotDisabled = userRepository.findAllByDisabled(false);

        for (User user : allNotDisabled) {
            ReentrantLock reentrantLock = new ReentrantLock();
            counterMap.put(user.getId().toString(), new QuotaLimiter(reentrantLock, user.getQuotaNumber()));
        }

        log.info("Finished user counter map loading");
    }
}
