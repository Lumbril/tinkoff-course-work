package com.example.authserver.services;

import com.example.authserver.dto.request.UserLoginRequest;
import com.example.authserver.dto.response.AccessAndRefreshJwtResponse;

public interface JwtService {
    AccessAndRefreshJwtResponse createAccessAndRefreshTokens(UserLoginRequest userLoginRequest);
}
