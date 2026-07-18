package com.gachi.server.global.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
        @Schema(description = "인증에 사용할 JWT 액세스 토큰") String accessToken
) {
}
