package com.vicarius.ratelimiter.exception;

public class UserNotException extends RuntimeException {

    public UserNotException(String message) {
        super(message);
    }
}
