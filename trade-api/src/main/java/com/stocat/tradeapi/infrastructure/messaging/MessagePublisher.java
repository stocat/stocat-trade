package com.stocat.tradeapi.infrastructure.messaging;

/**
 * 메시지를 큐에 발행하기 위한 인터페이스.
 * 실제 환경(RabbitMQ, SQS 등)과 Mock 환경의 구현을 추상화합니다.
 */
public interface MessagePublisher {
    
    /**
     * 지정된 토픽에 메시지를 발행합니다.
     *
     * @param topic   발행할 메시지의 토픽
     * @param message 발행할 메시지 객체
     */
    void publish(String topic, Object message);
}
