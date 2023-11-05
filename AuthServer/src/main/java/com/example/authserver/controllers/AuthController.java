package com.example.authserver.controllers;

import com.example.authserver.dto.request.RefreshRequest;
import com.example.authserver.dto.request.UserLoginRequest;
import com.example.authserver.dto.request.UserRegistrationRequest;
import com.example.authserver.dto.response.AccessAndRefreshJwtResponse;
import com.example.authserver.dto.response.ErrorResponse;
import com.example.authserver.exceptions.*;
import com.example.authserver.services.impl.JwtServiceImpl;
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
    private final JwtServiceImpl jwtService;

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

    @Operation(summary = "Получить Access JWT и Refresh JWT")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccessAndRefreshJwtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody UserLoginRequest userLoginRequest) {
        AccessAndRefreshJwtResponse response = jwtService.createAccessAndRefreshTokens(userLoginRequest);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Обновить токены")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccessAndRefreshJwtResponse.class)
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Validated @RequestBody RefreshRequest refreshRequest) {
        AccessAndRefreshJwtResponse response = jwtService.refreshTokens(refreshRequest);

        return ResponseEntity.ok().body(response);
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<?> handleUserExists(UserExistsException exception) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(UserPasswordException.class)
    public ResponseEntity<?> handleUserPasswordError(UserPasswordException exception) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(UserInvalidDataException.class)
    public ResponseEntity<?> handleUserInvalidData(UserInvalidDataException exception) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<?> handleServerError(ServerErrorException exception) {
        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException exception) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(exception.getMessage())
                        .build()
        );
    }
}
