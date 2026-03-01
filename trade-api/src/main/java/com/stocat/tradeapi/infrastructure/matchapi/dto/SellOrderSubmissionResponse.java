package com.stocat.tradeapi.infrastructure.matchapi.dto;

public record SellOrderSubmissionResponse(
        String code
) {
    // 임시 검증 로직
    public boolean isSuccess() {
        return "success".equals(code);
    }

    public boolean isRejected() {
        return "rejected".equals(code);
    }
}
