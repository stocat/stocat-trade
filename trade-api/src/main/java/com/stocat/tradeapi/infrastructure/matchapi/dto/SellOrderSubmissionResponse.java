package com.stocat.tradeapi.infrastructure.matchapi.dto;

public record SellOrderSubmissionResponse(
        String code
) {
    // 임시 검증 로직
    public boolean isSuccess() {
        return code.equals("success");
    }

    public boolean isRejected() {
        return code.equals("rejected");
    }
}
