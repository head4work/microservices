package com.head4work.companyservice.error;

import org.springframework.http.HttpStatus;

public class CustomResponseException extends Throwable {
    private final HttpStatus httpStatus;

    public CustomResponseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
