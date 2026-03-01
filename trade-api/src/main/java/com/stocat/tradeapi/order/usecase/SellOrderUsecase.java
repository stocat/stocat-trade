package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.SellOrderCommand;
import com.stocat.tradeapi.position.service.PositionQueryService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellOrderUsecase {
    private final QuoteApiClient quoteApiClient;
    private final MatchApiClient matchApiClient;
    private final SellOrderFacade sellOrderFacade;
    private final PositionQueryService positionQueryService;

    public OrderDto placeSellOrder(SellOrderCommand command) {
        AssetDto asset = quoteApiClient.fetchAsset(command.assetSymbol());
        validateSellOrder(command, asset);

        OrderDto orderDto = sellOrderFacade.processSellOrder(command, asset);
        matchApiClient.submitSellOrder(SellOrderSubmissionRequest.from(orderDto));
        return orderDto;
    }

    private void validateSellOrder(SellOrderCommand command, AssetDto asset) {
        PositionEntity position = positionQueryService
                .getUserPositionForUpdate(asset.id(), command.userId())
                .orElseThrow(() -> new ApiException(TradeErrorCode.POSITION_NOT_FOUND_FOR_SELL));

        isValidSellOrderQuantity(command.quantity(), position.getQuantity());
    }

    private void isValidSellOrderQuantity(BigDecimal sellQuantity, BigDecimal holdQuantity) {
        if (sellQuantity == null || sellQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_QUANTITY);
        }
        if (holdQuantity.compareTo(sellQuantity) < 0) {
            throw new ApiException(TradeErrorCode.INSUFFICIENT_POSITION_QUANTITY);
        }
    }
}
