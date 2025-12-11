package com.stocat.tradeapi.order.scheduler;

import com.stocat.common.domain.order.Order;
import com.stocat.tradeapi.order.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {
    private final OrderQueryService orderQueryService;
    private final OrderSubmitAsyncService orderSubmitAsyncService;

    @Scheduled(fixedDelay = 2000)
    public void retryCreatedOrders() {
        LocalDateTime stdTime = LocalDateTime.now().minusSeconds(2);
        List<Order> createdOrders = orderQueryService.findCreatedOrdersOlderThan(stdTime);

        createdOrders.forEach(orderSubmitAsyncService::submitOrderAsync);
    }
}
