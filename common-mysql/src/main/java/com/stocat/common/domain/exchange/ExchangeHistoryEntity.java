package com.stocat.common.domain.exchange;

import com.stocat.common.domain.BaseEntity;
import com.stocat.common.domain.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "exchange_histories")
public class ExchangeHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_currency", nullable = false, length = 10)
    private Currency fromCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_currency", nullable = false, length = 10)
    private Currency toCurrency;

    @Column(name = "from_amount", nullable = false, precision = 30, scale = 8)
    private BigDecimal fromAmount;

    @Column(name = "to_amount", nullable = false, precision = 30, scale = 8)
    private BigDecimal toAmount;

    @Column(name = "exchange_rate", nullable = false, precision = 30, scale = 8)
    private BigDecimal exchangeRate;
}
