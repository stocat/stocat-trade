package com.stocat.tradeapi.service;


import com.stocat.tradeapi.infrastructure.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import com.stocat.tradeapi.service.dto.OrderDto;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Quote API 서버와 통신하는 BFF 서버가 도입될 경우 Controller에서 필요한 Param을 모두 전달 받고 현재 Adapter는 제거
 */
@Service
@RequiredArgsConstructor
public class OrderServiceAdapter implements OrderServicePort {
    private final QuoteApiClient quoteApiClient;
    private final OrderService orderService;


    @Override
    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        AssetDto asset = quoteApiClient.fetchAsset(command.asset().ticker());

        BuyOrderCommand completeCommand = copyCommand(command, asset);

        return orderService.placeBuyOrder(completeCommand);
    }

    private BuyOrderCommand copyCommand(BuyOrderCommand origin, AssetDto asset) {
        return BuyOrderCommand.builder()
                .memberId(origin.memberId())
                .asset(asset)
                .quantity(origin.quantity())
                .price(origin.price())
                .build();
    }
}
