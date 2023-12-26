package com.vicarius.ratelimiter.service.limiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.locks.ReentrantLock;

@AllArgsConstructor
public class QuotaLimiter {
    @Getter
    @Setter
    private ReentrantLock reentrantLock;
    @Getter
    @Setter
    private Integer quotaNumber;
}
