package com.gachi.server.domain.user.dto;

import com.gachi.server.domain.user.entity.MobilityLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ParentProfileSaveRequest(

        @Schema(description = "부모님 성함", example = "김철수")
        @NotBlank
        String name,

        @Schema(description = "보행 수준", example = "SHORT_WALK")
        @NotNull
        MobilityLevel mobilityLevel,

        @Schema(description = "선택한 질환 목록")
        @NotNull
        @Valid
        List<ParentDiseaseRequest> diseases
) {
}
