package com.example.resourceserver.dto.response;

import com.example.resourceserver.entities.enums.FriendStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("status")
    private FriendStatus status;
}
