package com.stocat.authapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "이메일", example = "test@example.com")
        @NotBlank(message = "이메일은 필수 값입니다.")
        String email,
        @Schema(description = "비밀번호(평문)", example = "P@ssw0rd!")
        @NotBlank(message = "비밀번호는 필수 값입니다.")
        String password
) {
}
