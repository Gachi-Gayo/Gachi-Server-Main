package com.gachi.server.global.auth;

import com.gachi.server.domain.user.entity.User;
import com.gachi.server.domain.user.repository.UserRepository;
import com.gachi.server.global.exception.GachiException;
import com.gachi.server.global.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void refresh_rejectsAnAccessToken() {
        when(jwtUtil.validateRefreshToken("access-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("access-token"))
                .isInstanceOf(GachiException.class);
    }

    @Test
    void issueTokens_storesOnlyAHashOfTheRefreshToken() {
        User user = User.builder().id(1L).nickname("user").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(1L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("refresh-token");

        AuthService.TokenPair tokens = authService.issueTokens(1L);

        assertThat(tokens.accessToken()).isEqualTo("access-token");
        assertThat(user.getRefreshTokenHash()).isNotEqualTo("refresh-token");
        assertThat(user.getRefreshTokenHash()).hasSize(64);
    }

    @Test
    void refresh_rotatesTheRefreshTokenAndInvalidatesThePreviousOne() {
        JwtUtil realJwtUtil = new JwtUtil("01234567890123456789012345678901", 3_600_000, 1_209_600_000);
        AuthService service = new AuthService(userRepository, realJwtUtil);
        User user = User.builder().id(1L).nickname("user").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AuthService.TokenPair initial = service.issueTokens(1L);
        AuthService.TokenPair refreshed = service.refresh(initial.refreshToken());

        assertThat(refreshed.refreshToken()).isNotEqualTo(initial.refreshToken());
        assertThatThrownBy(() -> service.refresh(initial.refreshToken()))
                .isInstanceOf(GachiException.class);
    }
}
