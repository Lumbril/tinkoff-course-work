package com.example.resourceserver.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetRequest {
    @NotNull
    @JsonProperty("type_id")
    private Long typeId;

    @NotBlank
    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private Boolean gender;
}
