package com.stocat.authapi.infrastructure.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsulJwtSecretProviderTest {

    private static final String KEY_PATH = "config/common/secrets/jwt-secret";

    @Mock
    private ConsulClient consulClient;

    private ConsulJwtSecretProvider provider;

    @BeforeEach
    void setUp() {
        provider = new ConsulJwtSecretProvider(consulClient, KEY_PATH);
    }

    @Test
    void Base64_비밀키는_캐시에_저장된다() {
        String rawSecret = "01234567890123456789012345678901";
        String base64Secret = java.util.Base64.getEncoder().encodeToString(rawSecret.getBytes(StandardCharsets.UTF_8));
        when(consulClient.getKVValue(KEY_PATH)).thenReturn(responseWithDecodedValue(base64Secret));

        Key first = provider.getSigningKey();
        Key second = provider.getSigningKey();

        assertArrayEquals(Decoders.BASE64.decode(base64Secret), first.getEncoded());
        assertSame(first, second);
        verify(consulClient, times(1)).getKVValue(KEY_PATH);
    }

    @Test
    void Base64_해석이_안되면_평문으로_처리한다() {
        String plainSecret = "plain-secret-key-material-1234567890";
        when(consulClient.getKVValue(KEY_PATH)).thenReturn(responseWithDecodedValue(plainSecret));

        Key key = provider.getSigningKey();

        assertArrayEquals(plainSecret.getBytes(StandardCharsets.UTF_8), key.getEncoded());
    }

    @Test
    void 값이_없으면_예외를_던진다() {
        when(consulClient.getKVValue(KEY_PATH)).thenReturn(new Response<>(null, 0L, true, 0L));

        assertThrows(IllegalStateException.class, () -> provider.getSigningKey());
    }

    private Response<GetValue> responseWithDecodedValue(String decodedValue) {
        GetValue value = new GetValue() {
            @Override
            public String getDecodedValue() {
                return decodedValue;
            }
        };
        return new Response<>(value, 0L, true, 0L);
    }
}
