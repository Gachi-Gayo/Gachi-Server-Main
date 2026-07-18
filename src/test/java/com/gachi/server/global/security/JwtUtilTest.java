package com.gachi.server.global.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(
            "01234567890123456789012345678901", 3_600_000, 1_209_600_000
    );

    @Test
    void accessToken_isNotAcceptedAsRefreshToken() {
        String accessToken = jwtUtil.generateAccessToken(1L);

        assertThat(jwtUtil.validateAccessToken(accessToken)).isTrue();
        assertThat(jwtUtil.validateRefreshToken(accessToken)).isFalse();
    }

    @Test
    void refreshToken_isNotAcceptedAsAccessToken() {
        String refreshToken = jwtUtil.generateRefreshToken(1L);

        assertThat(jwtUtil.validateRefreshToken(refreshToken)).isTrue();
        assertThat(jwtUtil.validateAccessToken(refreshToken)).isFalse();
        assertThat(jwtUtil.getUserId(refreshToken)).isEqualTo(1L);
    }
}
