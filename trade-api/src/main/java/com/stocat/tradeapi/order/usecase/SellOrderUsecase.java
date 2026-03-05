package com.stocat.tradeapi.order.usecase;

import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.event.OrderPlacedEvent;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.SellOrderCommand;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 매도 주문 처리 유스케이스
 * <p>
 * 매도 주문 생성 요청을 받아 검증, 포지션 예약, 주문 생성, 이벤트 발행 등의 전체적인 흐름을 제어합니다.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class SellOrderUsecase {
    private final QuoteApiClient quoteApiClient;
    private final SellOrderFacade sellOrderFacade;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 매도 주문 생성
     * <p>
     * 1. 자산 정보 조회 및 수량 검증 2. 주문 생성 및 포지션 예약 (Facade 호출) 3. 주문 생성 완료 이벤트 발행 (OrderPlacedEvent)
     * </p>
     *
     * @param command 매도 주문 요청 정보
     * @return 생성된 주문 정보
     */
    @Transactional
    public OrderDto placeSellOrder(SellOrderCommand command) {
        isValidSellOrderQuantity(command.quantity());
        AssetDto asset = quoteApiClient.fetchAsset(command.assetSymbol());

        OrderDto orderDto = sellOrderFacade.processSellOrder(command, asset);

        eventPublisher.publishEvent(new OrderPlacedEvent(orderDto));
        return orderDto;
    }

    private void isValidSellOrderQuantity(BigDecimal sellQuantity) {
        if (sellQuantity == null || sellQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_QUANTITY);
        }
    }
}
