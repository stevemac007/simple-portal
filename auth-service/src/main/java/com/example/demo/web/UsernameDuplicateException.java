package com.example.demo.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UsernameDuplicateException extends RuntimeException {

    public UsernameDuplicateException(String username) {
        super("User: " +username +" already exists.");
    }
}
