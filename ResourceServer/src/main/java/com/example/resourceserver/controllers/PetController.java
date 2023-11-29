package com.example.resourceserver.controllers;

import com.example.resourceserver.dto.request.PetImageRequest;
import com.example.resourceserver.dto.request.PetRequest;
import com.example.resourceserver.dto.response.ErrorResponse;
import com.example.resourceserver.dto.response.PetResponse;
import com.example.resourceserver.entities.Pet;
import com.example.resourceserver.services.impl.MediaServiceImpl;
import com.example.resourceserver.services.impl.PetServiceImpl;
import com.example.resourceserver.utils.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/pet")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Pets", description = "Методы для питомцев")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PetController {
    private final PetServiceImpl petService;

    @Operation(summary = "Получить питомца по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PetResponse.class)
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
        Pet pet = petService.get(id);

        return ResponseEntity.ok().body(PetResponse.builder()
                .id(pet.getId())
                .typePet(pet.getTypePetId())
                .name(pet.getName())
                .gender(pet.getGender())
                .image(pet.getImage())
                .userId(pet.getUserId().getId())
                .build());
    }

    @Operation(summary = "Добавление питомца")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PetResponse.class)
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
    @PostMapping
    public ResponseEntity<?> doPost(@Validated @RequestBody PetRequest petRequest,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Pet pet = petService.create(petRequest, userDetails.getId());

        return ResponseEntity.ok().body(PetResponse.builder()
                        .id(pet.getId())
                        .typePet(pet.getTypePetId())
                        .name(pet.getName())
                        .gender(pet.getGender())
                        .image(pet.getImage())
                        .userId(userDetails.getId())
                        .build());
    }

    @Operation(summary = "Сохранить фотографию питомца")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PetResponse.class)
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
    @PostMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> saveImage(@PathVariable Long id,
                                      @ModelAttribute PetImageRequest petImageRequest,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Pet pet = petService.updateImage(id, userDetails.getId(), petImageRequest.getImage());

        return ResponseEntity.ok().body(PetResponse.builder()
                    .id(pet.getId())
                    .typePet(pet.getTypePetId())
                    .name(pet.getName())
                    .gender(pet.getGender())
                    .image(pet.getImage())
                    .userId(pet.getUserId().getId())
                .build());
    }

    @Operation(summary = "Удалить своего питомца по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json"
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> doDelete(@PathVariable Long id,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        petService.deleteByIdAndUser(id, userDetails.getId());

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestValue(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("Ошибка валидации")
                        .build()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("Питомец с таким id не найден")
                        .build()
        );
    }
}
