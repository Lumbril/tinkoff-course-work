package com.example.authserver.exceptions;

public class UserExistsException extends RuntimeException {
    public UserExistsException() {
        super("Пользователь с таким именем уже есть.");
    }
}
