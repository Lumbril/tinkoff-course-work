package com.example.authserver.services;

import com.example.authserver.entities.BlacklistRefreshToken;

public interface BlacklistRefreshTokenService {
    BlacklistRefreshToken save(String jti);
    boolean isExists(String jti);
}
