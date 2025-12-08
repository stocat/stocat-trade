package com.stocat.tradeapi.scheduler;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyRequest;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyResult;
import com.stocat.tradeapi.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSubmitAsyncService {
    private final MatchApiClient matchApiClient;
    private final OrderCommandService orderCommandService;

    @Async("orderTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void submitOrderAsync(Order order) {
        try {
            MatchBuyRequest request = MatchBuyRequest.builder()
                    .memberId(order.getMemberId())
                    .quantity(order.getQuantity())
                    .price(order.getPrice())
                    .build();

            MatchBuyResult result = matchApiClient.buy(request);

            if (result.isSuccess()) {
                orderCommandService.updateOrderStatus(order, OrderStatus.PENDING);
            }
            if (result.isRejected()) {
                orderCommandService.updateOrderStatus(order, OrderStatus.REJECTED);
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            log.info("주문 {}은 이미 다른 프로세스에서 업데이트되었습니다 (낙관락)", order.getId());
        } catch (IllegalStateException e) {
            log.warn("주문 {}의 상태를 변경할 수 없습니다: {}", order.getId(), e.getMessage());
        } catch (ApiException e) {
            log.error("주문 {}을 거래소에 전송하는 중 오류가 발생했습니다", order.getId(), e);
        }
    }
}