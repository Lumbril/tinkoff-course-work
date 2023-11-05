package com.example.authserver.services.impl;

import com.example.authserver.dto.TokenBody;
import com.example.authserver.dto.request.RefreshRequest;
import com.example.authserver.dto.request.UserLoginRequest;
import com.example.authserver.dto.response.AccessAndRefreshJwtResponse;
import com.example.authserver.entities.User;
import com.example.authserver.exceptions.InvalidTokenException;
import com.example.authserver.exceptions.ServerErrorException;
import com.example.authserver.exceptions.UserInvalidDataException;
import com.example.authserver.services.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${application.security.jwt.private-key}")
    private String PRIVATE_KEY;
    @Value("${application.security.jwt.public-key}")
    private String PUBLIC_KEY;
    @Value("${application.security.jwt.access-token.expiration}")
    private long ACCESS_TOKEN_EXPIRATION;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    private final UserServiceImpl userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public AccessAndRefreshJwtResponse createAccessAndRefreshTokens(UserLoginRequest userLoginRequest) {
        User user = getUserIfPasswordCorrect(userLoginRequest);

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return AccessAndRefreshJwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AccessAndRefreshJwtResponse refreshTokens(RefreshRequest refreshRequest) {
        Claims claims = getClaimsIfTokenValid(refreshRequest.getRefreshToken());
        TokenBody tokenBody = getTokenBody(claims);

        if (!tokenBody.getTokenType().equals("refresh")) {
            throw new InvalidTokenException();
        }

        User user = userService.getById(tokenBody.getUserId());

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return AccessAndRefreshJwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private User getUserIfPasswordCorrect(UserLoginRequest userLoginRequest) {
        User u;

        try {
            u = userService.getByUsername(userLoginRequest.getUsername());
        } catch (NoSuchElementException e) {
            throw new UserInvalidDataException();
        }

        if (!bCryptPasswordEncoder.matches(userLoginRequest.getPassword(), u.getPassword())) {
            throw new UserInvalidDataException();
        }

        return u;
    }

    private String createAccessToken(User user) {
        return buildToken(new HashMap<>(), user, "access", ACCESS_TOKEN_EXPIRATION);
    }

    private String createRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, "refresh", REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, User userDetails, String type, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setHeaderParam("typ", "JWT")
                .claim("user_id", userDetails.getId())
                .claim("username", userDetails.getUsername())
                .claim("token_type", type)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS512)
                .compact();
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

    private TokenBody getTokenBody(Claims claims) {
        try {
            return TokenBody.builder()
                    .userId(claims.get("user_id", Long.class))
                    .username(claims.get("username", String.class))
                    .tokenType(claims.get("token_type", String.class))
                    .jti(claims.getId())
                    .iat(claims.getIssuedAt())
                    .exp(claims.getExpiration())
                    .build();
        } catch (RequiredTypeException exception) {
            throw new InvalidTokenException();
        }
    }

    private Key getPrivateKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte [] bytes = Base64.getDecoder().decode(PRIVATE_KEY);
            PKCS8EncodedKeySpec keySpecPv = new PKCS8EncodedKeySpec(bytes);

            return keyFactory.generatePrivate(keySpecPv);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServerErrorException(e.getMessage());
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
