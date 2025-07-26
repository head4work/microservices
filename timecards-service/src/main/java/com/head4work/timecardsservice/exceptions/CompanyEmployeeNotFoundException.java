package com.head4work.timecardsservice.exceptions;

public class CompanyEmployeeNotFoundException extends RuntimeException {
    public CompanyEmployeeNotFoundException(String id) {
        super("Employee with id: %s not found ".formatted(id));
    }
}
