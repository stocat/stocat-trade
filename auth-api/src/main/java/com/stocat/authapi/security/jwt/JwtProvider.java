package com.stocat.authapi.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final JwtSecretProvider secretProvider;
    private final long accessExpireMinutes;
    private final long refreshExpireDays;

    public JwtProvider(
            JwtSecretProvider secretProvider,
            @Value("${jwt.access-exp-minutes:15}") long accessExpireMinutes,
            @Value("${jwt.refresh-exp-days:7}") long refreshExpireDays
    ) {
        this.secretProvider = secretProvider;
        this.accessExpireMinutes = accessExpireMinutes;
        this.refreshExpireDays = refreshExpireDays;
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessExpireMinutes, ChronoUnit.MINUTES)))
                .signWith(secretProvider.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshExpireDays, ChronoUnit.DAYS)))
                .signWith(secretProvider.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretProvider.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
