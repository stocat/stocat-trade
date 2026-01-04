package com.stocat.tradeapi.fill.service;

import com.stocat.common.domain.fill.TradeFillEntity;
import com.stocat.common.repository.TradeFillRepository;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TradeFillCommandService {
    private final TradeFillRepository tradeFillRepository;

    public TradeFillEntity createTradeFill(FillBuyOrderCommand command, OrderDto order) {
        TradeFillEntity fill = TradeFillEntity.builder()
                .orderId(command.orderId())
                .memberId(order.memberId())
                .assetId(command.assetId())
                .exchangeRateId(command.exchangeRateId())
                .realizedPnl(null)
                .side(command.side())
                .quantity(command.quantity())
                .price(command.price())
                .priceCurrency(command.priceCurrency())
                .feeAmount(command.feeAmount())
                .feeCurrency(command.feeCurrency())
                .quantity(command.quantity())
                .build();

        return tradeFillRepository.save(fill);
    }
}
