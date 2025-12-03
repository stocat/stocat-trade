package com.stocat.authapi.security.jwt;

import javax.crypto.SecretKey;

public interface JwtSecretProvider {

    /**
     * Load raw secret (may hit remote). Kept for compatibility.
     *
     * @return jwt secret
     */
    String loadSecret();

    /**
     * Return jwt signing key; initialize lazily if needed.
     *
     * @return signing key
     */
    SecretKey getSigningKey();
}
