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
@Table(name = "positions")
public class PositionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "asset_id")
    private Long assetId;

    private BigDecimal quantity;

    @Column(name = "avg_entry_price")
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
}
