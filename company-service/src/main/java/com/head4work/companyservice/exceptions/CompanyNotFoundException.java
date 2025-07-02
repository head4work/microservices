package com.head4work.companyservice.exceptions;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(String id) {
        super("Company with id: %s not found ".formatted(id));
    }
}
