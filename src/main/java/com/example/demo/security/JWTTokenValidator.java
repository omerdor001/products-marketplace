package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JWTTokenValidator implements TokenValidator {

    private final Key secretKey;
    private final long tokenValidityMillis;

    private static JWTTokenValidator instance;

    private JWTTokenValidator(String secret, long tokenValidityMillis) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.tokenValidityMillis = tokenValidityMillis;
    }

    public static synchronized JWTTokenValidator getInstance(String secret, long tokenValidityMillis) {
        if (instance == null) {
            instance = new JWTTokenValidator(secret, tokenValidityMillis);
        }
        return instance;
    }

    public String generateTokenForTesting(String resellerId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(resellerId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + tokenValidityMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}