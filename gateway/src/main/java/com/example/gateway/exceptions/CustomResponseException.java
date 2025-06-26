package com.example.gateway.exceptions;

import org.springframework.http.HttpStatus;


public class CustomResponseException extends RuntimeException {
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    private final HttpStatus httpStatus;

    public CustomResponseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
