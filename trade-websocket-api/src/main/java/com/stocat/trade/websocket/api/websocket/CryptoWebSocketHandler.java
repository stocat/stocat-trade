package com.stocat.trade.websocket.api.websocket;


import com.stocat.trade.websocket.api.service.RedisSubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class CryptoWebSocketHandler implements WebSocketHandler {
    private final RedisSubscriberService subscriber;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                        subscriber.subscribeTrades()
                                .map(session::textMessage)
                                .doOnCancel(() -> log.debug("WebSocket 세션 {}: 체결 데이터 스트림 취소", session.getId()))
                                .doOnError(e -> log.error("WebSocket 세션 {}: 체결 데이터 전송 중 오류", session.getId(), e))
                )
                .doOnSubscribe(sub -> log.debug("WebSocket 세션 {} 연결 처리 시작", session.getId()))
                .doFinally(signal -> log.debug("WebSocket 세션 {} 종료 (signal={})", session.getId(), signal));
    }
}