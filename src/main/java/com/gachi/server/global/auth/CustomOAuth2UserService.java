package com.gachi.server.global.auth;

import com.gachi.server.domain.user.entity.User;
import com.gachi.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        KakaoOAuth2Attributes attrs = KakaoOAuth2Attributes.from(oAuth2User.getAttributes());

        User user = userRepository.findByProviderId(attrs.getProviderId())
                .map(existingUser -> {
                    existingUser.updateOAuthProfile(attrs.getEmail(), attrs.getNickname(), attrs.getProfileImageUrl());
                    return existingUser;
                })
                .orElseGet(() -> userRepository.save(User.builder()
                        .providerId(attrs.getProviderId())
                        .email(attrs.getEmail())
                        .nickname(attrs.getNickname())
                        .profileImageUrl(attrs.getProfileImageUrl())
                        .build()));

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("USER_ID", user.getId());

        return new DefaultOAuth2User(List.of(), attributes, "id");
    }
}
