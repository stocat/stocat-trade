package com.stocat.tradeapi.fill.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import com.stocat.common.repository.CashBalanceRepository;
import com.stocat.common.repository.OrderRepository;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.order.usecase.BuyOrderUsecase;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BuyOrderDeadlockTest {

    @Autowired
    private BuyOrderUsecase buyOrderUsecase;

    @Autowired
    private BuyOrderExecutionUsecase buyOrderExecutionUsecase;

    @Autowired
    private CashBalanceRepository cashBalanceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private QuoteApiClient quoteApiClient;

    @MockitoBean
    private com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient matchApiClient;

    private Long userId = 100L;
    private Long assetId = 1L;
    private String assetSymbol = "BTC";
    private List<Long> orderIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 1. 기존 데이터 초기화
        orderRepository.deleteAll();
        cashBalanceRepository.deleteAll();

        // 2. 유저 현금 잔액 생성
        cashBalanceRepository.save(CashBalanceEntity.builder()
                .userId(userId)
                .currency(Currency.KRW)
                .balance(new BigDecimal("10000000"))
                .build());

        // 3. 3가지 카테고리 자산 준비 및 주문 생성 (규칙: 카테고리당 1회)
        AssetsCategory[] categories = {AssetsCategory.KOR_STOCK, AssetsCategory.US_STOCK, AssetsCategory.CRYPTO};
        String[] symbols = {"005930", "AAPL", "BTC"};

        for (int i = 0; i < categories.length; i++) {
            Long currentAssetId = (long) (i + 1);
            String currentSymbol = symbols[i];
            AssetsCategory currentCat = categories[i];

            AssetDto asset = AssetDto.builder()
                    .id(currentAssetId)
                    .symbol(currentSymbol)
                    .category(currentCat)
                    .currency(Currency.KRW)
                    .isActive(true)
                    .isDaily(true)
                    .koName("테스트자산-" + currentSymbol)
                    .build();

            when(quoteApiClient.fetchAsset(currentSymbol)).thenReturn(asset);
            when(quoteApiClient.fetchAssetById(currentAssetId)).thenReturn(asset);

            // 각 카테고리별로 주문 1개씩 생성
            BuyOrderCommand command = BuyOrderCommand.builder()
                    .userId(userId)
                    .assetSymbol(currentSymbol)
                    .orderType(OrderType.LIMIT)
                    .quantity(new BigDecimal("1"))
                    .price(new BigDecimal("10000"))
                    .tif(OrderTif.GTC)
                    .requestTime(LocalDateTime.now())
                    .build();

            OrderDto order = buyOrderUsecase.placeBuyOrder(command);
            orderIds.add(order.id());
        }
    }

    @Test
    @DisplayName("카테고리 매수 제한 규칙 하에서 동일 유저의 주문 생성과 체결이 겹쳐도 데드락이 없어야 한다")
    void concurrentOrderAndFillDeadlockTest() throws InterruptedException {
        int numberOfThreads = 12; // 3개 카테고리에 대해 Place/Fill 병렬 실행
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger deadlockCount = new AtomicInteger(0);
        String[] symbols = {"005930", "AAPL", "BTC"};

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    if (index % 2 == 0) {
                        // 1. 주문 생성 시도 (이미 카테고리당 1회 생성했으므로 비즈니스 에러가 나겠지만, 락은 잡음)
                        String symbol = symbols[(index / 2) % 3];
                        BuyOrderCommand command = BuyOrderCommand.builder()
                                .userId(userId)
                                .assetSymbol(symbol)
                                .orderType(OrderType.LIMIT)
                                .quantity(new BigDecimal("1"))
                                .price(new BigDecimal("10000"))
                                .tif(OrderTif.GTC)
                                .requestTime(LocalDateTime.now())
                                .build();
                        buyOrderUsecase.placeBuyOrder(command);
                    } else {
                        // 2. 주문 체결 시도 (기존에 생성된 3개의 주문 중 하나를 체결)
                        Long orderIdToFill = orderIds.get((index / 2) % orderIds.size());
                        FillBuyOrderCommand fillCommand = new FillBuyOrderCommand(
                                orderIdToFill,
                                (long) ((index / 2) % 3 + 1), // assetId
                                TradeSide.BUY,
                                new BigDecimal("1"),
                                new BigDecimal("10000"),
                                Currency.KRW,
                                BigDecimal.ZERO,
                                Currency.KRW,
                                LocalDateTime.now(),
                                null,
                                BigDecimal.ONE,
                                "PAIR"
                        );
                        buyOrderExecutionUsecase.fillBuyOrder(fillCommand);
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 비즈니스 예외(이미 매수함 등)는 무시, 오직 데드락만 체크
                    String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                    if (msg.contains("deadlock")) {
                        deadlockCount.incrementAndGet();
                        e.printStackTrace();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Then
        System.out.println("Deadlocks: " + deadlockCount.get());
        assertThat(deadlockCount.get()).isEqualTo(0);
    }
}
