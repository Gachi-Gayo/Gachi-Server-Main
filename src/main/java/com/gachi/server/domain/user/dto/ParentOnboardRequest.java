package com.gachi.server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ParentOnboardRequest(

        @Schema(description = "자녀에게 전달받은 초대 코드", example = "48213097")
        @NotBlank
        String inviteCode
) {
}
