package com.stocat.authapi.infrastructure.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.stocat.authapi.security.jwt.JwtSecretProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ConsulJwtSecretProvider implements JwtSecretProvider {

    private final ConsulClient consulClient;
    private final String keyPath;
    private final AtomicReference<SecretKey> cachedKey = new AtomicReference<>();

    public ConsulJwtSecretProvider(
            ConsulClient consulClient,
            @Value("${jwt.secret.key:config/common/secrets/jwt-secret}") String keyPath
    ) {
        this.consulClient = consulClient;
        this.keyPath = keyPath;
    }

    @PostConstruct
    public void init() {
        reload();
    }

    /**
     * Load raw secret (may hit remote). Kept for compatibility.
     *
     * @return jwt secret
     */
    public String loadSecret() {
        Response<GetValue> resp = consulClient.getKVValue(keyPath);
        GetValue value = resp.getValue();
        if (value == null) return null;
        String decoded = value.getDecodedValue();
        return decoded != null ? decoded.trim() : null;
    }

    @Override
    public SecretKey getSigningKey() {
        SecretKey key = cachedKey.get();
        if (key != null) return key;
        reload();
        SecretKey after = cachedKey.get();
        if (after == null) {
            throw new IllegalStateException("JWT secret is not available from Consul at '" + keyPath + "'");
        }
        return after;
    }


    /**
     * Reload secret from source and refresh cache.
     */
    public void reload() {
        String secret = loadSecret();
        if (secret == null || secret.isBlank()) {
            cachedKey.set(null);
            return;
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (RuntimeException e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        cachedKey.set(Keys.hmacShaKeyFor(keyBytes));
    }
}
