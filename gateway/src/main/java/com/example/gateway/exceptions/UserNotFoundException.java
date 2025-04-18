package com.example.gateway.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("User with id: %s not found ".formatted(id));
    }
}
