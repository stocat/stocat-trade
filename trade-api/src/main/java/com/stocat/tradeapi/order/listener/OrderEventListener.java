package com.stocat.tradeapi.order.listener;

import com.stocat.tradeapi.infrastructure.matchapi.MatchApiFacade;
import com.stocat.tradeapi.order.event.OrderCanceledEvent;
import com.stocat.tradeapi.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 주문 관련 이벤트 리스너
 * <p>
 * 주문 생성 완료(OrderPlacedEvent) 등의 이벤트를 수신하여 후속 작업을 처리합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final MatchApiFacade matchApiFacade;

    /**
     * 주문 생성 완료 이벤트 핸들러
     * <p>
     * 주문 트랜잭션이 성공적으로 커밋된 후(@TransactionalEventListener(phase = AFTER_COMMIT)) 실행됩니다. 외부 매칭 엔진으로 주문을 전송하며, 전송 실패 시 보상
     * 트랜잭션(주문 취소)을 수행합니다. 비동기(@Async)로 실행되어 사용자 응답 지연을 방지합니다.
     * </p>
     *
     * @param event 주문 생성 완료 이벤트
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("주문 생성 완료, 매칭 엔진에 전송 시작: orderId={}", event.orderDto().id());
        matchApiFacade.submitSellOrderWithRetry(event);
    }

    /**
     * 주문 취소 완료 이벤트 핸들러
     * <p>
     * 주문 취소 트랜잭션이 성공적으로 커밋된 후 실행됩니다. 외부 매칭 엔진으로 주문 취소 요청을 전송합니다. 비동기(@Async)로 실행되어 사용자 응답 지연을 방지합니다.
     * </p>
     *
     * @param event 주문 취소 완료 이벤트
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancellation(OrderCanceledEvent event) {
        log.info("주문 취소 완료, 매칭 엔진에 취소 요청 전송 시작: orderId={}", event.orderDto().id());
        matchApiFacade.submitCancelOrderWithRetry(event);
    }
}
