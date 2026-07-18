package com.gachi.server.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void refreshTokenApiDocumentationIncludesTheSuccessAndUnauthorizedContracts() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/auth/refresh'].post.summary").value("액세스 토큰 재발급"))
                .andExpect(jsonPath("$.paths['/api/auth/refresh'].post.responses['200'].description").value("재발급 성공"))
                .andExpect(jsonPath("$.paths['/api/auth/refresh'].post.responses['401'].description").value("리프레시 토큰이 없거나 유효하지 않음"))
                .andExpect(jsonPath("$.components.schemas.TokenResponse.properties.accessToken.description").value("인증에 사용할 JWT 액세스 토큰"));
    }
}
