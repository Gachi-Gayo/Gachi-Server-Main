package com.gachi.server.domain.user.service;

import com.gachi.server.domain.user.dto.ParentDiseaseResponse;
import com.gachi.server.domain.user.dto.ParentProfileResponse;
import com.gachi.server.domain.user.dto.ParentProfileSaveRequest;
import com.gachi.server.domain.user.entity.ParentDisease;
import com.gachi.server.domain.user.entity.ParentProfile;
import com.gachi.server.domain.user.entity.ProfileStatus;
import com.gachi.server.domain.user.entity.User;
import com.gachi.server.domain.user.repository.ParentDiseaseRepository;
import com.gachi.server.domain.user.repository.ParentProfileRepository;
import com.gachi.server.domain.user.repository.UserRepository;
import com.gachi.server.global.exception.ErrorCode;
import com.gachi.server.global.exception.GachiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentProfileService {

    private final ParentProfileRepository parentProfileRepository;
    private final ParentDiseaseRepository parentDiseaseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ParentProfileResponse saveDraft(Long childUserId, ParentProfileSaveRequest request) {
        return saveOrUpdate(childUserId, request, ProfileStatus.DRAFT);
    }

    @Transactional
    public ParentProfileResponse complete(Long childUserId, ParentProfileSaveRequest request) {
        return saveOrUpdate(childUserId, request, ProfileStatus.COMPLETED);
    }

    public ParentProfileResponse getDraft(Long childUserId) {
        User child = userRepository.findById(childUserId)
                .orElseThrow(() -> new GachiException(ErrorCode.USER_NOT_FOUND));

        ParentProfile profile = parentProfileRepository
                .findFirstByChildAndStatusOrderByIdDesc(child, ProfileStatus.DRAFT)
                .orElseThrow(() -> new GachiException(ErrorCode.PROFILE_NOT_FOUND));

        List<ParentDisease> diseases = parentDiseaseRepository.findByParentProfile(profile);

        return toResponse(profile, diseases);
    }

    private ParentProfileResponse saveOrUpdate(Long childUserId, ParentProfileSaveRequest request, ProfileStatus status) {
        User child = userRepository.findById(childUserId)
                .orElseThrow(() -> new GachiException(ErrorCode.USER_NOT_FOUND));

        ParentProfile profile = parentProfileRepository
                .findFirstByChildAndStatusOrderByIdDesc(child, ProfileStatus.DRAFT)
                .orElse(null);

        if (profile != null) {
            profile.update(request.name(), request.mobilityLevel(), status);
            parentDiseaseRepository.deleteByParentProfile(profile);
        } else {
            profile = ParentProfile.builder()
                    .child(child)
                    .name(request.name())
                    .mobilityLevel(request.mobilityLevel())
                    .status(status)
                    .build();
        }

        ParentProfile savedProfile = parentProfileRepository.save(profile);

        List<ParentDisease> diseases = request.diseases().stream()
                .map(diseaseRequest -> ParentDisease.builder()
                        .parentProfile(savedProfile)
                        .category(diseaseRequest.category())
                        .diseaseName(diseaseRequest.diseaseName())
                        .build())
                .toList();
        List<ParentDisease> savedDiseases = parentDiseaseRepository.saveAll(diseases);

        return toResponse(savedProfile, savedDiseases);
    }

    private ParentProfileResponse toResponse(ParentProfile profile, List<ParentDisease> diseases) {
        List<ParentDiseaseResponse> diseaseResponses = diseases.stream()
                .map(disease -> new ParentDiseaseResponse(disease.getCategory(), disease.getDiseaseName()))
                .toList();

        Long parentUserId = profile.getParent() != null ? profile.getParent().getId() : null;

        return new ParentProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getMobilityLevel(),
                profile.getStatus(),
                diseaseResponses,
                parentUserId
        );
    }
}
