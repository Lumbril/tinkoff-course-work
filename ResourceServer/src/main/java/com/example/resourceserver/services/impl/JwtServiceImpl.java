package com.example.resourceserver.services.impl;

import com.example.resourceserver.dto.TokenBody;
import com.example.resourceserver.exceptions.InvalidTokenException;
import com.example.resourceserver.exceptions.ServerErrorException;
import com.example.resourceserver.services.JwtService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${application.security.jwt.public-key}")
    private String PUBLIC_KEY;

    @Override
    public TokenBody getTokenBody(String token) {
        Claims claims = getClaimsIfTokenValid(token);

        return TokenBody.builder()
                .userId(claims.get("user_id", Long.class))
                .username(claims.get("username", String.class))
                .tokenType(claims.get("token_type", String.class))
                .jti(claims.getId())
                .iat(claims.getIssuedAt())
                .exp(claims.getExpiration())
                .build();
    }

    private Claims getClaimsIfTokenValid(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException |
                 UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException |
                 IllegalArgumentException exception) {
            throw new InvalidTokenException();
        }
    }

    private Key getPublicKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte [] bytes = Base64.getDecoder().decode(PUBLIC_KEY);
            X509EncodedKeySpec keySpecPv = new X509EncodedKeySpec(bytes);

            return keyFactory.generatePublic(keySpecPv);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServerErrorException(e.getMessage());
        }
    }
}
