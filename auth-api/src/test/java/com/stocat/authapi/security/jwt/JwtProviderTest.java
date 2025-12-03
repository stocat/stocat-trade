package com.stocat.authapi.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        JwtSecretProvider secretProvider = fixedSecret();
        jwtProvider = new JwtProvider(secretProvider, 5, 2);
    }

    @Test
    void 액세스토큰은_주체와_클레임을_정확히_담는다() {
        String token = jwtProvider.createAccessToken("42", Map.of(
                JwtClaimKeys.EMAIL, "user@stocat.com",
                JwtClaimKeys.ROLE, "USER"
        ));

        Claims claims = jwtProvider.parse(token);

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get(JwtClaimKeys.EMAIL)).isEqualTo("user@stocat.com");
        assertThat(claims.get(JwtClaimKeys.ROLE)).isEqualTo("USER");
        assertThat(claims.getExpiration().toInstant()).isAfter(Instant.now());
    }

    @Test
    void 리프레시토큰_만료시간은_설정값에_따라_계산된다() {
        String token = jwtProvider.createRefreshToken("42");

        Claims claims = jwtProvider.parse(token);

        Instant now = Instant.now();
        Instant expiration = claims.getExpiration().toInstant();
        assertThat(expiration).isAfter(now.plus(1, ChronoUnit.DAYS));
        assertThat(expiration).isBefore(now.plus(3, ChronoUnit.DAYS));
    }

    private JwtSecretProvider fixedSecret() {
        byte[] keyBytes = "stocat-test-secret-key-material-123456".getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return new JwtSecretProvider() {
            @Override
            public String loadSecret() {
                return null;
            }

            @Override
            public javax.crypto.SecretKey getSigningKey() {
                return (javax.crypto.SecretKey) key;
            }
        };
    }
}
