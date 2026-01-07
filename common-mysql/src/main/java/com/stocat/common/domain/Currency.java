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

    public AssetsCategory getCategory() {
        if (category == null) {
            throw new IllegalStateException("해당 통화에 매칭되는 자산 카테고리가 없습니다.");
        }

        return category;
    }
}
