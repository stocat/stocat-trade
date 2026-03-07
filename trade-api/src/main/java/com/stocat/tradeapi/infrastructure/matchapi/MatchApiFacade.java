package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.order.event.OrderCanceledEvent;
import com.stocat.tradeapi.order.event.OrderPlacedEvent;
import com.stocat.tradeapi.order.usecase.SellOrderFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * 매칭 엔진 API 호출을 위한 Facade
 * <p>
 * 외부 매칭 엔진과의 통신을 담당하며, 재시도(Retry) 및 복구(Recover) 로직을 캡슐화합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchApiFacade {

    private final MatchApiClient matchApiClient;
    private final SellOrderFacade sellOrderFacade;

    /**
     * 매도 주문 전송 (재시도 적용)
     * <p>
     * 최대 시도 횟수: 3회, 재시도 간격: 1초 (매 시도마다 2배씩 증가)
     * </p>
     *
     * @param event 주문 생성 이벤트
     */
    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void submitSellOrderWithRetry(OrderPlacedEvent event) {
        log.info("매칭 엔진 전송 시도: orderId={}", event.orderDto().id());
        matchApiClient.submitSellOrder(SellOrderSubmissionRequest.from(event.orderDto()));
        log.info("매칭 엔진 전송 성공: orderId={}", event.orderDto().id());
    }

    /**
     * 매도 주문 전송 실패 시 복구 처리 (Recover)
     * <p>
     * 모든 재시도 시도가 실패했을 때 Spring Retry에 의해 자동으로 호출됩니다. 주문을 취소하고 포지션 예약을 해제하는 보상 트랜잭션을 수행합니다.
     *
     * @param e     발생한 예외
     * @param event 주문 생성 이벤트
     */
    @Recover
    public void recoverSellOrderSubmission(Exception e, OrderPlacedEvent event) {
        log.error("매칭 엔진 전송 최종 실패. 주문을 취소합니다: orderId={}", event.orderDto().id(), e);
        sellOrderFacade.compensateSellOrder(event.orderDto().id(), event.orderDto().userId());
    }

    /**
     * 주문 취소 전송 (재시도 적용)
     * <p>
     * 최대 시도 횟수: 3회, 재시도 간격: 1초 (매 시도마다 2배씩 증가)
     * </p>
     *
     * @param event 주문 취소 이벤트
     */
    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void submitCancelOrderWithRetry(OrderCanceledEvent event) {
        log.info("매칭 엔진 주문 취소 전송 시도: orderId={}", event.orderDto().id());
        matchApiClient.submitCancelOrder(event.orderDto().id());
        log.info("매칭 엔진 주문 취소 전송 성공: orderId={}", event.orderDto().id());
    }

    /**
     * 주문 취소 정보 전송 실패 시 복구 처리 (Recover)
     * <p>
     * 모든 재시도 시도가 실패했을 때 Spring Retry에 의해 자동으로 호출됩니다. DB 상태는 이미 CANCELED이지만 외부 전송이 실패한 상황이므로, 관리자 개입이 필요함을 로그로 남깁니다.
     *
     * @param e     발생한 예외
     * @param event 주문 취소 이벤트
     */
    @Recover
    public void recoverCancelOrderSubmission(Exception e, OrderCanceledEvent event) {
        // 이미 취소 API 가 전달되었을 수도 있음
        log.error("매칭 엔진 주문 취소 전송 최종 실패 (데이터 불일치 위험). 수동 확인 필요: orderId={}", event.orderDto().id(), e);
    }
}
