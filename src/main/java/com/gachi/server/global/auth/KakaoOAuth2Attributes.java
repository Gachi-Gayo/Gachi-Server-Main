package com.gachi.server.global.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class KakaoOAuth2Attributes {

    private final String providerId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    @SuppressWarnings("unchecked")
    public static KakaoOAuth2Attributes from(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
        String nickname = profile != null ? (String) profile.get("nickname") : "사용자";
        String profileImageUrl = profile != null ? (String) profile.get("profile_image_url") : null;

        return new KakaoOAuth2Attributes(providerId, email, nickname, profileImageUrl);
    }
}
