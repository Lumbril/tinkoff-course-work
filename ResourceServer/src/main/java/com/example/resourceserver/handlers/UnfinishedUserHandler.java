package com.example.resourceserver.handlers;

import com.example.resourceserver.dto.response.ErrorResponse;
import com.example.resourceserver.exceptions.UnfinishedUserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UnfinishedUserHandler {
    @ExceptionHandler(UnfinishedUserException.class)
    public ResponseEntity<?> handleUnfinishedUser(UnfinishedUserException exception) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handler(Exception exception) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }
}
