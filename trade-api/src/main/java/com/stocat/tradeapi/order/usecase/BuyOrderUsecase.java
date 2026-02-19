package com.stocat.tradeapi.order.usecase;

import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuyOrderUsecase {
    private final QuoteApiClient quoteApiClient;
    private final BuyOrderFacade buyOrderFacade;

    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        AssetDto asset = quoteApiClient.fetchAsset(command.assetSymbol());
        validateAsset(asset);

        return buyOrderFacade.processBuyOrder(command, asset);
    }

    private void validateAsset(AssetDto asset) {
        if (asset == null) {
            throw new ApiException(TradeErrorCode.ASSET_NOT_FOUND);
        }
        if (!asset.isDaily()) {
            throw new ApiException(TradeErrorCode.NOT_DAILY_PICK_ASSET);
        }
    }
}
