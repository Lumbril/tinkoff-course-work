package com.example.resourceserver.dto.response;

import com.example.resourceserver.entities.TypePet;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("type_pet")
    private TypePet typePet;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private Boolean gender;

    @JsonProperty("image")
    private String image;

    @JsonProperty("user_id")
    private Long userId;
}
