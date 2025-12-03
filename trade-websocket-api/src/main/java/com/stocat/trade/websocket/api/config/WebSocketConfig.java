package com.stocat.trade.websocket.api.config;

import com.stocat.trade.websocket.api.websocket.CryptoWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Bean
    public HandlerMapping webSocketMapping(CryptoWebSocketHandler handler) {
        return new SimpleUrlHandlerMapping(
                Map.of("/ws/crypto/trades", handler),
                Ordered.HIGHEST_PRECEDENCE
        );
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
