package com.head4work.payrollservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomResponseException extends RuntimeException {
    private final HttpStatus httpStatus;

    public CustomResponseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
