package com.stocat.tradeapi.order.usecase;

import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.position.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BuyOrderUsecase {
    private final OrderService orderService;
    private final PositionService positionService;
    private final QuoteApiClient quoteApiClient;

    @Transactional
    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        AssetDto asset = quoteApiClient.fetchAsset(command.assetSymbol());
        validateAsset(asset);

        // TODO: 포지션 예약 테이블에 데이터 생성 필요

        return orderService.placeBuyOrder(command, asset);
    }

    private void validateAsset(AssetDto asset) {
        if (!asset.isDaily()) {
            throw new ApiException(TradeErrorCode.NOT_DAILY_PICK_ASSET);
        }
    }
}
