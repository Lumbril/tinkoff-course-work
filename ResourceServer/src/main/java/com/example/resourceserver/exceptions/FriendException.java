package com.example.resourceserver.exceptions;

public class FriendException extends RuntimeException {
    public FriendException() {
        super();
    }

    public FriendException(String message) {
        super(message);
    }
}
