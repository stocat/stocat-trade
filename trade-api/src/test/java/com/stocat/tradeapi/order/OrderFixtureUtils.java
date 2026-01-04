package com.stocat.tradeapi.order;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class OrderFixtureUtils {
    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 1000L;
    private static final Long ASSET_ID = 1L;

    public static BuyOrderCommand createBuyOrderCommand() {
        return createBuyOrderCommand(createUsdAssetDto());
    }

    public static BuyOrderCommand createBuyOrderCommand(AssetDto asset) {
        return BuyOrderCommand.builder()
                .userId(USER_ID)
                .assetSymbol("NVDA")
                .orderType(OrderType.LIMIT)
                .price(BigDecimal.valueOf(200))
                .quantity(BigDecimal.valueOf(100))
                .requestTime(LocalDateTime.of(2025, 12, 1, 0, 0, 0))
                .build();
    }

    public static AssetDto createUsdAssetDto() {
        return AssetDto.builder()
                .id(ASSET_ID)
                .symbol("NVDA")
                .category(AssetsCategory.USD)
                .currency(Currency.USD)
                .isActive(true)
                .isDaily(true)
                .koName("엔비디아")
                .usName("NVIDIA")
                .build();
    }

    public static AssetDto createCryptoAssetDto() {
        return AssetDto.builder()
                .id(ASSET_ID + 1)
                .symbol("BTC/KRW")
                .category(AssetsCategory.CRYPTO)
                .currency(Currency.KRW)
                .isActive(true)
                .isDaily(true)
                .koName("비트코인")
                .usName("BTC")
                .build();
    }

    public static Order createBuyOrder(OrderStatus status) {
        return Order.builder()
                .id(ORDER_ID)
                .userId(USER_ID)
                .assetId(ASSET_ID)
                .side(TradeSide.BUY)
                .type(OrderType.LIMIT)
                .status(status)
                .quantity(BigDecimal.valueOf(100))
                .price(BigDecimal.valueOf(200))
                .build();
    }

    public static Order createBuyOrder(BuyOrderCommand command) {
        return Order.builder()
                .id(ORDER_ID)
                .userId(command.userId())
                .assetId(ASSET_ID)
                .side(TradeSide.BUY)
                .type(command.orderType())
                .status(OrderStatus.PENDING)
                .quantity(command.quantity())
                .price(command.price())
                .build();
    }
}
