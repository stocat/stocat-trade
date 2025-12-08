package com.stocat.tradeapi.infrastructure.dto;

public record MatchBuyResult (
        String result
)
{
    // 임시 검증 로직
    public boolean isSuccess() {
        return result.equals("success");
    }

    public boolean isRejected() {
        return result.equals("rejected");
    }

    public boolean isFailure() {
        return result.equals("failure");
    }


}
