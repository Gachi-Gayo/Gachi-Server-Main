package com.gachi.server.global.auth;

import com.gachi.server.global.common.ApiResponse;
import com.gachi.server.global.exception.ErrorCode;
import com.gachi.server.global.exception.GachiException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "액세스 토큰 재발급", description = "쿠키의 리프레시 토큰을 검증하고 새 액세스 토큰과 리프레시 토큰을 발급한다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "리프레시 토큰이 없거나 유효하지 않음")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new GachiException(ErrorCode.UNAUTHORIZED);
        }
        AuthService.TokenPair tokens = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie(tokens.refreshToken()).toString())
                .body(ApiResponse.ok(new TokenResponse(tokens.accessToken())));
    }

    static ResponseCookie refreshCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/api/auth")
                .sameSite("Lax")
                .build();
    }
}
