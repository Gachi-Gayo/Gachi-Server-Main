package com.gachi.server.domain.user.controller;

import com.gachi.server.domain.user.dto.ChildOnboardResponse;
import com.gachi.server.domain.user.dto.ParentOnboardRequest;
import com.gachi.server.domain.user.service.UserService;
import com.gachi.server.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "자녀 온보딩", description = "로그인한 사용자를 자녀 역할로 지정하고, 부모 계정과 연동할 초대 코드를 발급한다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "발급 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음")
    @PostMapping("/child/onboard")
    public ResponseEntity<ApiResponse<ChildOnboardResponse>> childOnboard(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(userService.childOnboard(userId)));
    }

    @Operation(summary = "부모 온보딩", description = "로그인한 사용자를 부모 역할로 지정하고, 자녀가 공유한 초대 코드로 자녀 계정과 연동한다. 자녀가 미리 작성해 둔 부모 건강 프로필이 있으면 함께 연동된다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "연동 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음 또는 유효하지 않은 초대 코드")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 다른 부모와 연결된 자녀")
    @PostMapping("/parent/onboard")
    public ResponseEntity<ApiResponse<Void>> parentOnboard(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid ParentOnboardRequest request
    ) {
        userService.parentOnboard(userId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
