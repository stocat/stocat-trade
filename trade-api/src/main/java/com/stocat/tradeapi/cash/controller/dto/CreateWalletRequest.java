package com.stocat.tradeapi.cash.controller.dto;

import com.stocat.common.domain.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateWalletRequest(
        @NotNull
        @Schema(description = "생성할 지갑 통화", example = "USD")
        Currency currency
) {
}