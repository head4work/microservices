package com.head4work.employeeservice.exceptions;

import org.springframework.http.HttpStatus;

public class CustomResponseException extends RuntimeException {
    private final HttpStatus httpStatus;

    public CustomResponseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
