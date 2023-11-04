package com.example.authserver.controllers;

import com.example.authserver.dto.request.UserRegistrationRequest;
import com.example.authserver.dto.response.ErrorResponse;
import com.example.authserver.exceptions.UserExistsException;
import com.example.authserver.exceptions.UserPasswordException;
import com.example.authserver.services.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "API for registration and generation JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserServiceImpl userService;

    @Operation(summary = "Регистрация")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201"
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Validated @RequestBody UserRegistrationRequest user) {
        userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<?> handle(UserExistsException exception) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(UserPasswordException.class)
    public ResponseEntity<?> handle(UserPasswordException exception) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }
}
