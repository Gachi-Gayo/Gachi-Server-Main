package com.gachi.server.global.auth;

import com.gachi.server.domain.user.entity.User;
import com.gachi.server.domain.user.repository.UserRepository;
import com.gachi.server.global.exception.ErrorCode;
import com.gachi.server.global.exception.GachiException;
import com.gachi.server.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public TokenPair issueTokens(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GachiException(ErrorCode.USER_NOT_FOUND));
        String refreshToken = jwtUtil.generateRefreshToken(userId);
        user.updateRefreshTokenHash(hash(refreshToken));
        return new TokenPair(jwtUtil.generateAccessToken(userId), refreshToken);
    }

    @Transactional
    public TokenPair refresh(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new GachiException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GachiException(ErrorCode.UNAUTHORIZED));
        if (!hash(refreshToken).equals(user.getRefreshTokenHash())) {
            throw new GachiException(ErrorCode.UNAUTHORIZED);
        }

        return issueTokens(userId);
    }

    private String hash(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public record TokenPair(String accessToken, String refreshToken) {
    }
}
