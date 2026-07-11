package com.gachi.server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChildOnboardResponse(

        @Schema(description = "자녀 계정 초대 코드 (부모에게 공유)", example = "48213097")
        String inviteCode
) {
}
