package com.stocat.common.domain;

public enum Currency {
    KRW(AssetsCategory.KRW),
    USD(AssetsCategory.USD),
    BTC(null),
    USDT(null),
    ;

    private final AssetsCategory category;

    Currency(AssetsCategory category) {
        this.category = category;
    }

    /**
     * Currency에 해당하는 자산 카테고리를 반환합니다.
     * Asset을 거래할 때 어떤 자산(현금) 카테고리를 예약하거나 수정해야하는 지 알기 위해 사용하는 메소드입니다.
     */
    public AssetsCategory getCategory() {
        if (category == null) {
            throw new IllegalStateException("해당 통화에 매칭되는 자산 카테고리가 없습니다.");
        }

        return category;
    }
}
