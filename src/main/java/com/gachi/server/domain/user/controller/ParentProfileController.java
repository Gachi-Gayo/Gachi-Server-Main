package com.gachi.server.domain.user.controller;

import com.gachi.server.domain.user.dto.ParentProfileResponse;
import com.gachi.server.domain.user.dto.ParentProfileSaveRequest;
import com.gachi.server.domain.user.service.ParentProfileService;
import com.gachi.server.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ParentProfile", description = "부모 건강 프로필 API")
@RestController
@RequestMapping("/api/parent-profiles")
@RequiredArgsConstructor
public class ParentProfileController {

    private final ParentProfileService parentProfileService;

    @Operation(summary = "부모 건강 프로필 임시 저장", description = "자녀가 부모의 보행 능력, 건강 상태 등을 임시 저장한다. 이미 작성 중인 임시저장 프로필이 있으면 덮어쓴다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "임시 저장 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음")
    @PostMapping("/draft")
    public ResponseEntity<ApiResponse<ParentProfileResponse>> saveDraft(
            @AuthenticationPrincipal Long childUserId,
            @RequestBody @Valid ParentProfileSaveRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                parentProfileService.saveDraft(childUserId, request)));
    }

    @Operation(summary = "부모 건강 프로필 임시 저장 조회", description = "자녀가 임시 저장해 둔 부모 건강 프로필을 이어서 작성하기 위해 조회한다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음 또는 임시 저장된 프로필 없음")
    @GetMapping("/draft")
    public ResponseEntity<ApiResponse<ParentProfileResponse>> getDraft(
            @AuthenticationPrincipal Long childUserId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                parentProfileService.getDraft(childUserId)));
    }

    @Operation(summary = "부모 건강 프로필 완성", description = "자녀가 작성 중이던 부모 건강 프로필을 최종 완료 상태로 저장한다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "완료 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음")
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<ParentProfileResponse>> complete(
            @AuthenticationPrincipal Long childUserId,
            @RequestBody @Valid ParentProfileSaveRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                parentProfileService.complete(childUserId, request)));
    }
}
