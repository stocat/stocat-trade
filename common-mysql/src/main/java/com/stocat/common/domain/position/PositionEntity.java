package com.stocat.common.domain.position;

import com.stocat.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public void subtract(@NonNull BigDecimal quantity) {

        if (this.quantity.compareTo(quantity) < 0) {
            throw new IllegalStateException("보유 수량보다 많이 뺄 수 없습니다.");
        }

        this.quantity = this.quantity.subtract(quantity);
    }

    public void add(@NonNull BigDecimal additionalQuantity, @NonNull BigDecimal additionalAvgEntryPrice) {
        if (additionalQuantity.signum() <= 0) {
            throw new IllegalArgumentException("추가 수량은 0보다 커야 합니다.");
        }

        BigDecimal originalTotalAvgEntryPrice = this.quantity.multiply(this.avgEntryPrice);
        BigDecimal additionalTotalAvgEntryPrice = additionalQuantity.multiply(additionalAvgEntryPrice);

        BigDecimal totalPrice = originalTotalAvgEntryPrice.add(additionalTotalAvgEntryPrice);
        BigDecimal totalQuantity = this.quantity.add(additionalQuantity);

        BigDecimal accumulatedAvgEntryPrice =
                totalPrice.divide(totalQuantity, RoundingMode.HALF_UP);

        this.quantity = totalQuantity;
        this.avgEntryPrice = accumulatedAvgEntryPrice;
    }
}
