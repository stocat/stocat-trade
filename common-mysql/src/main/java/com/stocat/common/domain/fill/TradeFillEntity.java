package com.stocat.common.domain.fill;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "trade_fills")
@Entity
public class TradeFillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long assetId;

    // TODO: ERD 수정
    @Column(nullable = false)
    private Long exchangeRateId;

    @Column(nullable = true)
    private BigDecimal realizedPnl;

    // TODO: 상세정보

    @Column(nullable = false)
    private TradeSide side;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Currency priceCurrency;

    @Column(nullable = false)
    private BigDecimal feeAmount;

    @Column(nullable = false)
    private Currency feeCurrency;

    @Column(nullable = false)
    private LocalDateTime executedAt;
}
