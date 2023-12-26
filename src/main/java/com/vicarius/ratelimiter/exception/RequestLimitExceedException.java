package com.vicarius.ratelimiter.exception;

public class RequestLimitExceedException extends RuntimeException {

    public RequestLimitExceedException(String message) {
        super(message);
    }
}
