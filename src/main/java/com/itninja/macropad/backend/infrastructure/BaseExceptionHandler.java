package com.itninja.macropad.backend.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

@Slf4j
@Primary
@ControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<Object> handle(AsyncRequestTimeoutException exception) {
        return toTimeoutResponseEntity();
    }


    protected ResponseEntity<Object> toTimeoutResponseEntity() {
        return ResponseEntity
                .status(205)
                .body(null);
    }
}
