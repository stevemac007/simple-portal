package com.example.demo.web;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(Long userId ) {
        super("User: " +userId +" not found.");
    }
}
