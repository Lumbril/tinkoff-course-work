package com.example.authserver.exceptions;

public class UserPasswordException extends RuntimeException {
    public UserPasswordException(String message) {
        super(message);
    }
}
