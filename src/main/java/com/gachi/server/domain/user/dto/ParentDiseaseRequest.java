package com.gachi.server.domain.user.dto;

import com.gachi.server.domain.user.entity.DiseaseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParentDiseaseRequest(

        @Schema(description = "질환 대분류")
        @NotNull
        DiseaseCategory category,

        @Schema(description = "질환명", example = "고혈압")
        @NotBlank
        String diseaseName
) {
}
