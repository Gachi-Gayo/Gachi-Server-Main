package com.gachi.server.global.security;

import com.gachi.server.global.common.ApiResponse;
import com.gachi.server.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint(objectMapper);

    @Nested
    @DisplayName("commence")
    class Commence {

        @Test
        @DisplayName("인증되지 않은 요청이면 401 상태코드와 JSON 형식의 실패 응답을 반환한다")
        void commence_writesUnauthorizedJsonResponse() throws Exception {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);

            InsufficientAuthenticationException authException =
                    new InsufficientAuthenticationException("인증 정보가 없습니다.");

            // when
            entryPoint.commence(request, response, authException);
            printWriter.flush();

            // then
            verify(response).setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
            verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
            verify(response).setCharacterEncoding("UTF-8");

            String expectedBody = objectMapper.writeValueAsString(ApiResponse.fail(ErrorCode.UNAUTHORIZED));
            assertThat(stringWriter.toString()).isEqualTo(expectedBody);
        }

        @Test
        @DisplayName("응답 body에 success:false 와 UNAUTHORIZED 메시지가 포함된다")
        void commence_writesFailureMessage() throws Exception {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);

            InsufficientAuthenticationException authException =
                    new InsufficientAuthenticationException("인증 정보가 없습니다.");

            // when
            entryPoint.commence(request, response, authException);
            printWriter.flush();

            // then
            ApiResponse<?> parsed = objectMapper.readValue(stringWriter.toString(), ApiResponse.class);
            assertThat(parsed.success()).isFalse();
            assertThat(parsed.message()).isEqualTo(ErrorCode.UNAUTHORIZED.getMessage());
        }
    }
}
