package com.example.resourceserver.services;

import com.example.resourceserver.dto.TokenBody;

public interface JwtService {
    TokenBody getTokenBody(String token);
}
