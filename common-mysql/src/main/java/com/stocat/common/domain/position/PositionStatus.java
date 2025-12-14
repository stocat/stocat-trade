package com.stocat.common.domain.position;

import lombok.Getter;

@Getter
public enum PositionStatus {
    OPEN("보유"),
    CLOSED("수익 실현 완료"),
    EXPIRED("만료");

    private final String description;

    PositionStatus(String description) {
        this.description = description;
    }
}

