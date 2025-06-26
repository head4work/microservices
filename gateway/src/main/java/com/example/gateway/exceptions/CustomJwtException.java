package com.example.gateway.exceptions;

import org.springframework.http.HttpStatus;

public class CustomJwtException extends RuntimeException {
    private final HttpStatus status;

    public CustomJwtException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
    public CustomJwtException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() {
        return status;
    }
}