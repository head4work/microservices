package com.head4work.companyservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmptyCompanyException extends RuntimeException {
    private final HttpStatus httpStatus;

    public EmptyCompanyException() {
        super("No employees assigned for Company");
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

}
