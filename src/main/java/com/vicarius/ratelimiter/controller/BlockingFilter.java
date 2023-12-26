package com.vicarius.ratelimiter.controller;

import com.vicarius.ratelimiter.service.UserLoader;
import com.vicarius.ratelimiter.service.limiter.QuotaLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class BlockingFilter extends OncePerRequestFilter {

    @Autowired
    private VicariusProperties vicariusProperties;

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        log.info("Inside Once Per Request Filter originated by request {}", url);
        String userId = retrieveUserId(url);
        String method = request.getMethod();

        QuotaLimiter quotaLimiter;

        try {
            UserLoader.reentrantLock.lock();
            quotaLimiter = UserLoader.counterMap.get(userId);
        } finally {
            UserLoader.reentrantLock.unlock();
        }

        boolean containsFreedApi = false;

        for (FreedApi freedApi : vicariusProperties.getFreedApi()) {
            if (url.contains(freedApi.getUrl()) && method.contains(freedApi.getMethod())) {
                containsFreedApi = true;
                break;
            }
        }

        if (!containsFreedApi && quotaLimiter != null) {
            ReentrantLock reentrantLock = quotaLimiter.getReentrantLock();

            try {
                reentrantLock.lock();
                Integer quotaNumber = quotaLimiter.getQuotaNumber();
                if (quotaNumber > 0) {
                    quotaNumber--;
                    log.info("UserId {} has {} remaining quota", userId, quotaNumber);

                    quotaLimiter.setQuotaNumber(quotaNumber);

                    filterChain.doFilter(request, response);
                } else {
                    log.info("Reached limit on userId {}", userId);
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                }
            } finally {
                reentrantLock.unlock();
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private static String retrieveUserId(String url) {
        String[] split = url.split("/");

        String userId = "";

        for (String part : split) {
            if (part.matches(UUID_REGEX)) {
                userId = part;
            }
        }

        return userId;
    }
}
