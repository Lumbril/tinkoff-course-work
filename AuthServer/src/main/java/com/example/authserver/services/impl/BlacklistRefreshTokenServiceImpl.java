package com.example.authserver.services.impl;

import com.example.authserver.entities.BlacklistRefreshToken;
import com.example.authserver.repositories.BlacklistRefreshTokenRepository;
import com.example.authserver.services.BlacklistRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlacklistRefreshTokenServiceImpl implements BlacklistRefreshTokenService {
    private final BlacklistRefreshTokenRepository blacklistRefreshTokenRepository;

    @Override
    @Transactional
    public BlacklistRefreshToken save(String jti) {
        return blacklistRefreshTokenRepository.save(
                BlacklistRefreshToken.builder()
                        .jti(jti)
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(String jti) {
        return blacklistRefreshTokenRepository.existsByJti(jti);
    }
}
