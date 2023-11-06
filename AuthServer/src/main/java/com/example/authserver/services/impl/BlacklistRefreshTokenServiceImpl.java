package com.example.authserver.services.impl;

import com.example.authserver.entities.BlacklistRefreshToken;
import com.example.authserver.repositories.BlacklistRefreshTokenRepository;
import com.example.authserver.services.BlacklistRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistRefreshTokenServiceImpl implements BlacklistRefreshTokenService {
    private final BlacklistRefreshTokenRepository blacklistRefreshTokenRepository;

    @Override
    public BlacklistRefreshToken save(String jti) {
        return blacklistRefreshTokenRepository.save(
                BlacklistRefreshToken.builder()
                        .jti(jti)
                        .build());
    }

    @Override
    public boolean isExists(String jti) {
        return blacklistRefreshTokenRepository.existsByJti(jti);
    }
}
