package com.example.resourceserver.exceptions;

public class UnfinishedUserException extends RuntimeException {
    public UnfinishedUserException() {
        super("Завершите регистрацию");
    }
}
