package com.gachi.server.global.auth;

import com.gachi.server.domain.user.entity.User;
import com.gachi.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestOperations;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestOperations restOperations;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Nested
    @DisplayName("loadUser")
    class LoadUser {

        @Test
        @DisplayName("기존 사용자가 다시 로그인하면 최신 카카오 프로필 정보로 갱신한다")
        void loadUser_updatesExistingUserWithLatestOAuthProfile() {
            // given
            User existingUser = User.builder()
                    .id(1L)
                    .providerId("12345")
                    .email("old@example.com")
                    .nickname("기존 닉네임")
                    .profileImageUrl("https://example.com/old-profile.png")
                    .build();
            Map<String, Object> oauthAttributes = Map.of(
                    "id", 12345L,
                    "kakao_account", Map.of(
                            "email", "new@example.com",
                            "profile", Map.of(
                                    "nickname", "새 닉네임",
                                    "profile_image_url", "https://example.com/new-profile.png"
                            )
                    )
            );
            OAuth2UserRequest userRequest = new OAuth2UserRequest(
                    ClientRegistration.withRegistrationId("kakao")
                            .clientId("client-id")
                            .clientSecret("client-secret")
                            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                            .redirectUri("http://localhost/login/oauth2/code/kakao")
                            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                            .tokenUri("https://kauth.kakao.com/oauth/token")
                            .userInfoUri("https://kapi.kakao.com/v2/user/me")
                            .userNameAttributeName("id")
                            .clientName("Kakao")
                            .build(),
                    new OAuth2AccessToken(
                            OAuth2AccessToken.TokenType.BEARER,
                            "access-token",
                            Instant.now(),
                            Instant.now().plusSeconds(3600)
                    )
            );
            customOAuth2UserService.setRestOperations(restOperations);
            when(restOperations.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(oauthAttributes));
            when(userRepository.findByProviderId("12345")).thenReturn(Optional.of(existingUser));

            // when
            customOAuth2UserService.loadUser(userRequest);

            // then
            assertThat(existingUser.getEmail()).isEqualTo("new@example.com");
            assertThat(existingUser.getNickname()).isEqualTo("새 닉네임");
            assertThat(existingUser.getProfileImageUrl()).isEqualTo("https://example.com/new-profile.png");
        }
    }
}
