package com.stocat.authapi.infrastructure.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsulJwtSecretWatcherTest {

    private static final String KEY_PATH = "config/common/secrets/jwt-secret";

    @Mock
    private ConsulClient consulClient;
    @Mock
    private ConsulJwtSecretProvider secretProvider;

    private ConsulJwtSecretWatcher watcher;

    @BeforeEach
    void setUp() {
        watcher = new ConsulJwtSecretWatcher(consulClient, secretProvider, KEY_PATH);
    }

    @Test
    void 변경인덱스가_달라지면_reload를_호출한다() {
        when(consulClient.getKVValue(KEY_PATH))
                .thenReturn(responseWithModifyIndex(10L))
                .thenReturn(responseWithModifyIndex(10L))
                .thenReturn(responseWithModifyIndex(11L));

        watcher.pollForChanges();
        watcher.pollForChanges();
        watcher.pollForChanges();

        verify(secretProvider, times(2)).reload();
    }

    @Test
    void 값이_없으면_reload하지_않는다() {
        when(consulClient.getKVValue(KEY_PATH)).thenReturn(new Response<>(null, 0L, true, 0L));

        watcher.pollForChanges();

        verifyNoInteractions(secretProvider);
    }

    private Response<GetValue> responseWithModifyIndex(long modifyIndex) {
        GetValue value = new GetValue() {
            @Override
            public long getModifyIndex() {
                return modifyIndex;
            }
        };
        return new Response<>(value, 0L, true, 0L);
    }
}
