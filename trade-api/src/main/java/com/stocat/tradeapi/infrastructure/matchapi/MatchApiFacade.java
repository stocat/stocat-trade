package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.order.service.dto.OrderDto;
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
     * @param orderDto 전송할 주문 정보
     */
    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void submitSellOrderWithRetry(OrderDto orderDto) {
        log.info("매칭 엔진 전송 시도: orderId={}", orderDto.id());
        matchApiClient.submitSellOrder(SellOrderSubmissionRequest.from(orderDto));
        log.info("매칭 엔진 전송 성공: orderId={}", orderDto.id());
    }

    /**
     * 매도 주문 전송 실패 시 복구 처리 (Recover)
     * <p>
     * 모든 재시도 시도가 실패했을 때 Spring Retry에 의해 자동으로 호출됩니다. 주문을 취소하고 포지션 예약을 해제하는 보상 트랜잭션을 수행합니다.
     *
     * @param e        발생한 예외
     * @param orderDto 주문 정보
     */
    @Recover
    public void recoverSellOrderSubmission(Exception e, OrderDto orderDto) {
        log.error("매칭 엔진 전송 최종 실패. 주문을 취소합니다: orderId={}", orderDto.id(), e);
        sellOrderFacade.cancelSellOrder(orderDto.id());
    }
}
