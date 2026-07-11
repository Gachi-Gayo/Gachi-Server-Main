package com.gachi.server.domain.user.dto;

import com.gachi.server.domain.user.entity.MobilityLevel;
import com.gachi.server.domain.user.entity.ProfileStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ParentProfileResponse(

        @Schema(description = "부모 건강 프로필 ID")
        Long id,

        @Schema(description = "부모님 성함", example = "김철수")
        String name,

        @Schema(description = "보행 수준", example = "SHORT_WALK")
        MobilityLevel mobilityLevel,

        @Schema(description = "프로필 작성 상태", example = "DRAFT")
        ProfileStatus status,

        @Schema(description = "선택한 질환 목록")
        List<ParentDiseaseResponse> diseases,

        @Schema(description = "연동된 부모 계정 사용자 ID (미연동 시 null)")
        Long parentUserId
) {
}
