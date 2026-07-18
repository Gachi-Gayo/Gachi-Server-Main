package com.gachi.server.global.auth;

import com.gachi.server.global.common.ApiResponse;
import com.gachi.server.global.exception.ErrorCode;
import com.gachi.server.global.exception.GachiException;
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
