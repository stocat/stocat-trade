package com.stocat.tradeapi.exchange.controller.dto;

import com.stocat.tradeapi.exchange.usecase.dto.CurrencyExchangeCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CurrencyExchangeRequest(
        @NotBlank
        @Schema(description = "환율 고정 키 (미리보기 응답에서 발급)")
        String rateLockKey
) {

    public CurrencyExchangeCommand toCommand(Long userId) {
        return new CurrencyExchangeCommand(userId, rateLockKey);
    }
}