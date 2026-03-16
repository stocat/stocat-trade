package com.stocat.tradeapi.infrastructure.messaging;

import com.stocat.tradeapi.order.service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock Publisher: 테스트용 또는 로컬 환경을 위해 메시지 발행 역할을 시뮬레이션합니다.
 * 내부적으로 즉시 컨슈머를 호출하여 큐의 역할을 대신합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MockMessagePublisher implements MessagePublisher {

    private final MockMatchApiConsumer mockMatchApiConsumer;

    /**
     * 지정된 토픽에 메시지를 발행합니다.
     * Mock 환경에서는 즉시 컨슈머의 메서드를 호출하여 메시지를 전달합니다.
     *
     * @param topic 발행할 메시지의 토픽
     * @param message 발행할 메시지 객체
     */
    @Override
    public void publish(String topic, Object message) {
        log.info("[Mock Queue] 토픽 '{}'에 메시지 발행 완료: {}", topic, message);

        switch (topic) {
            case "match-engine-order-topic":
                mockMatchApiConsumer.consumeOrder((OrderDto) message);
                break;
            case "match-engine-cancel-order-topic":
                mockMatchApiConsumer.consumeCancelOrder((Long) message);
                break;
            default:
                log.warn("[Mock Queue] 처리할 수 없는 토픽입니다: {}", topic);
        }
    }
}
