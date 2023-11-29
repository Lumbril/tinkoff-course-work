package com.example.resourceserver.controllers;

import com.example.resourceserver.dto.response.ErrorResponse;
import com.example.resourceserver.dto.response.PetResponse;
import com.example.resourceserver.dto.response.TypePetResponse;
import com.example.resourceserver.dto.response.UserResponse;
import com.example.resourceserver.entities.Pet;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.services.impl.PetServiceImpl;
import com.example.resourceserver.services.impl.UserServiceImpl;
import com.example.resourceserver.utils.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User", description = "Методы пользователей")
@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final PetServiceImpl petService;

    @Operation(summary = "Получить информацию о себе")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.getById(userDetails.getId());

        return ResponseEntity.ok().body(UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                .build());
    }

    @Operation(summary = "Получить список своих питомцев")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PetResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/me/pets")
    public ResponseEntity<?> myPets(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(getListPetResponse(userDetails.getId()));
    }

    @Operation(summary = "Получить информацию о пользователе по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> doGet(@PathVariable Long id) {
        User user = userService.getById(id);

        return ResponseEntity.ok().body(UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build());
    }

    @Operation(summary = "Получить список питомцев у пользователя с id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PetResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/{id}/pets")
    public ResponseEntity<?> getUserPets(@PathVariable Long id) {
        return ResponseEntity.ok().body(getListPetResponse(id));
    }

    @Operation(summary = "Получить список пользователей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<User> users = userService.getAll();
        List<UserResponse> userResponseList = users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build())
                .toList();

        return ResponseEntity.ok().body(userResponseList);
    }

    private List<PetResponse> getListPetResponse(Long id) {
        List<Pet> pets = petService.getAllByUser(id);
        List<PetResponse> petResponseList = pets.stream()
                .map(pet -> PetResponse.builder()
                        .id(pet.getId())
                        .typePet(pet.getTypePetId())
                        .name(pet.getName())
                        .gender(pet.getGender())
                        .image(pet.getImage())
                        .userId(pet.getUserId().getId())
                        .build())
                .toList();

        return petResponseList;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("Пользователь с таким id не найден")
                        .build()
        );
    }
}
