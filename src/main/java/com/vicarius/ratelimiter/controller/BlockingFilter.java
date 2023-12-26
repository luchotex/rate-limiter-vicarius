package com.vicarius.ratelimiter.controller;

import com.vicarius.ratelimiter.exception.RequestLimitExceedException;
import com.vicarius.ratelimiter.service.UserLoader;
import com.vicarius.ratelimiter.service.limiter.QuotaLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class BlockingFilter extends OncePerRequestFilter {

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        log.info("Inside Once Per Request Filter originated by request {}", url);
        String userId = retrieveUserId(request, url);


        QuotaLimiter quotaLimiter = UserLoader.counterMap.get(userId);

        ReentrantLock reentrantLock = quotaLimiter.getReentrantLock();

        try {
            reentrantLock.lock();
            Integer currentQuotaNumber = quotaLimiter.getQuotaNumber();
            if (currentQuotaNumber > 0) {
                currentQuotaNumber--;
                quotaLimiter.setQuotaNumber(currentQuotaNumber);
            } else {
                throw new RequestLimitExceedException("the user can't access API's anymore");
            }
        } finally {
            reentrantLock.unlock();
        }

        filterChain.doFilter(request, response);
    }

    private static String retrieveUserId(HttpServletRequest request, String url) {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
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
