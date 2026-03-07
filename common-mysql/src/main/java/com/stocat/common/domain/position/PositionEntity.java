package com.stocat.common.domain.position;

import com.stocat.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

    @Column(name = "reserved_quantity", nullable = false)
    private BigDecimal reservedQuantity;

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
                .reservedQuantity(BigDecimal.ZERO)
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

    // 매도 주문 시 수량 예약
    public void reserve(@NonNull BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("예약 수량은 0보다 커야 합니다.");
        }
        BigDecimal available = this.quantity.subtract(this.reservedQuantity);
        if (available.compareTo(amount) < 0) {
            throw new IllegalStateException("주문 가능 수량이 부족합니다.");
        }
        this.reservedQuantity = this.reservedQuantity.add(amount);
    }

    // 매도 주문 취소/실패 시 예약 해제
    public void release(@NonNull BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("해제 수량은 0보다 커야 합니다.");
        }
        if (this.reservedQuantity.compareTo(amount) < 0) {
            throw new IllegalStateException("예약된 수량보다 많이 해제할 수 없습니다.");
        }
        this.reservedQuantity = this.reservedQuantity.subtract(amount);
    }

    // 매도 체결 시 수량 확정 차감 (총 수량과 예약 수량 모두 감소)
    public void settle(@NonNull BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("체결 수량은 0보다 커야 합니다.");
        }
        if (this.reservedQuantity.compareTo(amount) < 0) {
            throw new IllegalStateException("예약된 수량보다 많이 체결할 수 없습니다.");
        }
        if (this.quantity.compareTo(amount) < 0) {
            throw new IllegalStateException("보유 수량보다 많이 체결할 수 없습니다.");
        }
        this.quantity = this.quantity.subtract(amount);
        this.reservedQuantity = this.reservedQuantity.subtract(amount);
    }
}
