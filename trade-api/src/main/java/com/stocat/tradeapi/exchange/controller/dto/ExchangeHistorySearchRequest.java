package com.stocat.tradeapi.exchange.controller.dto;

import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record ExchangeHistorySearchRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "조회 시작일 (포함)", example = "2024-01-01")
        LocalDate from,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "조회 종료일 (포함)", example = "2024-12-31")
        LocalDate to
) {
    public ExchangeHistoryQuery toQuery(Long userId, Pageable pageable) {
        return new ExchangeHistoryQuery(
                userId,
                from != null ? from.atStartOfDay() : null,
                to != null ? to.atTime(LocalTime.MAX) : null,
                pageable
        );
    }
}