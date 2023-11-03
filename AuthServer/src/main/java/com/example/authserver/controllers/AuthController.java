package com.example.authserver.controllers;

import com.example.authserver.dto.request.UserRegistrationRequest;
import com.example.authserver.services.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "API for registration and generation JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserServiceImpl userService;

    @Operation(summary = "Регистрация")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Validated @RequestBody UserRegistrationRequest user) {
        userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
