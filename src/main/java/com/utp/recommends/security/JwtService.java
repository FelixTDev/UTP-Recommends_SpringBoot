package com.utp.recommends.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${app.security.jwt.secret}")
    private String secret;

    @Value("${app.security.jwt.expiration-minutes}")
    private long expirationMinutes;

    public String generateToken(Long userId, String rol, String estado, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(email)
            .claims(Map.of("userId", userId, "rol", rol, "estado", estado))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
            .signWith(getSignInKey())
            .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public boolean isTokenValid(String token, String email) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject().equals(email) && claims.getExpiration().after(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes;
        if (secret.matches("^[A-Za-z0-9+/=]+$") && secret.length() % 4 == 0) {
            try {
                keyBytes = Decoders.BASE64.decode(secret);
                return Keys.hmacShaKeyFor(keyBytes);
            } catch (IllegalArgumentException ignored) {
            }
        }
        keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
