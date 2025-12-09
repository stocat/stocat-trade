package com.stocat.common.domain.position;

import com.stocat.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "positions")
public class PositionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "asset_id")
    private Long assetId;

    @Enumerated(EnumType.STRING)
    private PositionStatus status;

    @Enumerated(EnumType.STRING)
    private PositionDirection direction;

    private BigDecimal quantity;

    @Column(name = "avg_entry_price")
    private BigDecimal avgEntryPrice;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public static PositionEntity from(Long userId,
                                      Long assetId,
                                      PositionStatus status,
                                      PositionDirection direction,
                                      BigDecimal quantity,
                                      BigDecimal avgEntryPrice,
                                      LocalDateTime expiresAt,
                                      LocalDateTime openedAt,
                                      LocalDateTime closedAt) {

        return PositionEntity.builder()
                .userId(userId)
                .assetId(assetId)
                .status(status)
                .direction(direction)
                .quantity(quantity)
                .avgEntryPrice(avgEntryPrice)
                .openedAt(LocalDateTime.now())
                .build();
    }
}
