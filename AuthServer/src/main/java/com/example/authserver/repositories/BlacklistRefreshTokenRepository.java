package com.example.authserver.repositories;

import com.example.authserver.entities.BlacklistRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRefreshTokenRepository extends JpaRepository<BlacklistRefreshToken, Long> {
    boolean existsByJti(String jti);
}
