package com.example.resourceserver.exceptions;

public class IncorrectFileFormat extends RuntimeException {
    public IncorrectFileFormat() {
        super("Неверный формат файла");
    }

    public IncorrectFileFormat(String message) {
        super(message);
    }
}
