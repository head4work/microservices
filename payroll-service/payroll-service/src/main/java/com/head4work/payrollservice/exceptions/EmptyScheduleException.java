package com.head4work.payrollservice.exceptions;

import org.springframework.http.HttpStatus;

public class EmptyScheduleException extends RuntimeException {
    private final HttpStatus httpStatus;

    public EmptyScheduleException() {
        super("No employees assigned for schedule");
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
