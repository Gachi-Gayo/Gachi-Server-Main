package com.gachi.server.domain.user.dto;

import com.gachi.server.domain.user.entity.DiseaseCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record ParentDiseaseResponse(

        @Schema(description = "질환 대분류")
        DiseaseCategory category,

        @Schema(description = "질환명", example = "고혈압")
        String diseaseName
) {
}
