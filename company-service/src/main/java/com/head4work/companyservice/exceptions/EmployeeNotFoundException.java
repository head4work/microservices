package com.head4work.companyservice.exceptions;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String id) {
        super("Employee with id: %s not found ".formatted(id));
    }
}
