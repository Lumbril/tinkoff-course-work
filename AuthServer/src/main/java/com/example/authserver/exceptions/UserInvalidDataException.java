package com.example.authserver.exceptions;

public class UserInvalidDataException extends RuntimeException {
    public UserInvalidDataException() {
        super("Неверные данные");
    }
}
