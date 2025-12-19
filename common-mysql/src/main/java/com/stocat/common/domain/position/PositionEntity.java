package com.stocat.common.domain.position;

import com.stocat.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(
        name = "positions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_position_user_asset", columnNames = {"user_id", "asset_id"})
        }
)
public class PositionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "asset_id", nullable = false)
    private Long assetId;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "avg_entry_price", nullable = false)
    private BigDecimal avgEntryPrice;

    public static PositionEntity create(Long userId,
                                        Long assetId,
                                        BigDecimal quantity,
                                        BigDecimal avgEntryPrice
    ) {

        return PositionEntity.builder()
                .userId(userId)
                .assetId(assetId)
                .quantity(quantity)
                .avgEntryPrice(avgEntryPrice)
                .build();
    }

    public void substract(BigDecimal quantity) {
        // TODO
    }

    public void add(BigDecimal additionalQuantity, BigDecimal additionalAvgEntryPrice) {
        // TODO
    }
}
