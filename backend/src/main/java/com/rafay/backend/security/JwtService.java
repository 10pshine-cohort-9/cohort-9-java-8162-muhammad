package com.rafay.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;
    }

    public String generateToken(String email) {

        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(
                        new Date(now.getTime() + expiration)
                )
                .signWith(secretKey)
                .compact();
    }

    public Optional<String> extractSubject(String token) {

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.ofNullable(
                    claims.getSubject()
            );

        } catch (JwtException | IllegalArgumentException e) {

            return Optional.empty();
        }
    }
}