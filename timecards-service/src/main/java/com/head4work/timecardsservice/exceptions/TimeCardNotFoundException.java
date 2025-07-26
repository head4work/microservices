package com.head4work.timecardsservice.exceptions;

public class TimeCardNotFoundException extends RuntimeException {
    public TimeCardNotFoundException(String id) {
        super("TimeCard with id: %s not found ".formatted(id));
    }
}
