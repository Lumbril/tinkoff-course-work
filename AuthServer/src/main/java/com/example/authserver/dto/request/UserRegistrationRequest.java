package com.example.authserver.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    @NotBlank
    @JsonProperty(value = "username", required = true)
    private String username;

    @NotBlank
    @JsonProperty(value = "password", required = true)
    private String password;
}
