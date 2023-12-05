package com.example.resourceserver.controllers;

import com.example.resourceserver.dto.response.ErrorResponse;
import com.example.resourceserver.dto.response.TypePetResponse;
import com.example.resourceserver.entities.TypePet;
import com.example.resourceserver.services.impl.TypePetServiceImpl;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/type_pet")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Type pets", description = "Методы для типов домашних животных")
@CrossOrigin("*")
@RequiredArgsConstructor
public class TypePetController {
    private final TypePetServiceImpl typePetService;

    @Operation(summary = "Получить тип дом. животного по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TypePetResponse.class)
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
        TypePet typePet = typePetService.get(id);

        if (typePet == null) {
            throw new NoSuchElementException();
        }

        return ResponseEntity.ok().body(TypePetResponse.builder()
                        .id(typePet.getId())
                        .name(typePet.getName())
                        .build());
    }

    @Operation(summary = "Получить типы дом. животных")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = TypePetResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/list")
    public ResponseEntity<?> getList() {
        List<TypePet> typePetList = typePetService.getAll();

        List<TypePetResponse> typePetResponseList = typePetList.stream()
                .map(typePet -> TypePetResponse.builder()
                        .id(typePet.getId())
                        .name(typePet.getName())
                        .build()
                ).toList();

        return ResponseEntity.ok().body(typePetResponseList);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("Тип домашнего животного с таким id не найден")
                        .build()
        );
    }
}
