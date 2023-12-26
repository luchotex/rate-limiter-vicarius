package com.vicarius.ratelimiter.service.limiter;

import com.vicarius.ratelimiter.model.User;
import com.vicarius.ratelimiter.repository.UserRepository;
import com.vicarius.ratelimiter.service.UserLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerLoader {

    private final UserRepository userRepository;

    @Scheduled(fixedRateString = "${vicarius.scheduled-fixed-rate}")
    public void updateQuotaLimiters() {
        log.info("Started updating quota limiter");
        Set<Map.Entry<String, QuotaLimiter>> entries = UserLoader.counterMap.entrySet();

        for (Map.Entry<String, QuotaLimiter> entry : entries) {
            String userId = entry.getKey();
            QuotaLimiter quotaLimiter = entry.getValue();
            ReentrantLock reentrantLock = quotaLimiter.getReentrantLock();
            try {
                reentrantLock.lock();
                User user = userRepository.findByIdAndDisabled(UUID.fromString(userId), false).orElseThrow();
                user.setQuotaNumber(quotaLimiter.getQuotaNumber());
                userRepository.save(user);
                log.info("Updated userid {} quota number", user.getId());
            } finally {
                reentrantLock.unlock();
            }
        }

        log.info("Finished updating quota limiter");
    }

    @EventListener(ContextClosedEvent.class)
    public void onContextClosedEvent() {
        updateQuotaLimiters();
    }
}
