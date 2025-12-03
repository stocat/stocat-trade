package com.stocat.authapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthErrorCodeResponse(
        @Schema(description = "에러 코드", example = "10002") int code,
        @Schema(description = "에러 메시지", example = "이메일 또는 비밀번호가 올바르지 않습니다.") String message
) {}

