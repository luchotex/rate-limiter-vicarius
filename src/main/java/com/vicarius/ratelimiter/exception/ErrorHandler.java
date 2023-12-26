package com.vicarius.ratelimiter.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> userNotFoundException(Exception ex) {
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> genericException(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, Exception ex, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        String path = servletWebRequest.getRequest().getRequestURI();

        ErrorModel errorModel = ErrorModel.builder()
                .timestamp(LocalDateTime.now()).status(status.value())
                .error(status.getReasonPhrase()).message(ex.getMessage()).path(path).build();

        return new ResponseEntity<>(errorModel, status);
    }
}
