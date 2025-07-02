package com.head4work.employeeservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserHasNoCompaniesException extends RuntimeException {
    private final HttpStatus httpStatus;

    public UserHasNoCompaniesException() {
        super("You have no companies in the company list");
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

}
