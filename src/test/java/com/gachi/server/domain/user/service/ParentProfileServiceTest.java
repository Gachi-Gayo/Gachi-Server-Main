package com.gachi.server.domain.user.service;

import com.gachi.server.domain.user.dto.ParentDiseaseRequest;
import com.gachi.server.domain.user.dto.ParentProfileResponse;
import com.gachi.server.domain.user.dto.ParentProfileSaveRequest;
import com.gachi.server.domain.user.entity.DiseaseCategory;
import com.gachi.server.domain.user.entity.MobilityLevel;
import com.gachi.server.domain.user.entity.ParentDisease;
import com.gachi.server.domain.user.entity.ParentProfile;
import com.gachi.server.domain.user.entity.ProfileStatus;
import com.gachi.server.domain.user.entity.User;
import com.gachi.server.domain.user.repository.ParentDiseaseRepository;
import com.gachi.server.domain.user.repository.ParentProfileRepository;
import com.gachi.server.domain.user.repository.UserRepository;
import com.gachi.server.global.exception.ErrorCode;
import com.gachi.server.global.exception.GachiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParentProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParentProfileRepository parentProfileRepository;

    @Mock
    private ParentDiseaseRepository parentDiseaseRepository;

    @InjectMocks
    private ParentProfileService parentProfileService;

    private User child;
    private ParentProfileSaveRequest request;

    private void setUpChildAndRequest() {
        child = User.builder()
                .id(1L)
                .nickname("자녀")
                .build();
        request = new ParentProfileSaveRequest(
                "김철수",
                MobilityLevel.SHORT_WALK,
                List.of(new ParentDiseaseRequest(DiseaseCategory.CHRONIC, "고혈압"))
        );
    }

    @Nested
    @DisplayName("saveDraft")
    class SaveDraft {

        @Test
        @DisplayName("기존 DRAFT 프로필이 없으면 신규로 생성한다")
        void saveDraft_createsNewProfileWhenNoDraftExists() {
            // given
            setUpChildAndRequest();
            when(userRepository.findById(1L)).thenReturn(Optional.of(child));
            when(parentProfileRepository.findFirstByChildAndStatusOrderByIdDesc(child, ProfileStatus.DRAFT))
                    .thenReturn(Optional.empty());
            when(parentProfileRepository.save(any(ParentProfile.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(parentDiseaseRepository.saveAll(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ParentProfileResponse response = parentProfileService.saveDraft(1L, request);

            // then
            ArgumentCaptor<ParentProfile> profileCaptor = ArgumentCaptor.forClass(ParentProfile.class);
            verify(parentProfileRepository).save(profileCaptor.capture());
            ParentProfile savedProfile = profileCaptor.getValue();
            assertThat(savedProfile.getStatus()).isEqualTo(ProfileStatus.DRAFT);
            assertThat(savedProfile.getName()).isEqualTo("김철수");
            assertThat(savedProfile.getMobilityLevel()).isEqualTo(MobilityLevel.SHORT_WALK);
            assertThat(savedProfile.getChild()).isEqualTo(child);

            verify(parentDiseaseRepository).saveAll(any());

            assertThat(response.name()).isEqualTo("김철수");
            assertThat(response.mobilityLevel()).isEqualTo(MobilityLevel.SHORT_WALK);
            assertThat(response.status()).isEqualTo(ProfileStatus.DRAFT);
            assertThat(response.diseases()).hasSize(1);
            assertThat(response.diseases().get(0).category()).isEqualTo(DiseaseCategory.CHRONIC);
            assertThat(response.diseases().get(0).diseaseName()).isEqualTo("고혈압");
            assertThat(response.parentUserId()).isNull();
        }

        @Test
        @DisplayName("기존 DRAFT 프로필이 있으면 재사용하여 갱신한다")
        void saveDraft_updatesExistingDraftProfile() {
            // given
            setUpChildAndRequest();
            ParentProfile existingProfile = ParentProfile.builder()
                    .id(50L)
                    .child(child)
                    .parent(null)
                    .name("이전이름")
                    .mobilityLevel(MobilityLevel.NORMAL)
                    .status(ProfileStatus.DRAFT)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(child));
            when(parentProfileRepository.findFirstByChildAndStatusOrderByIdDesc(child, ProfileStatus.DRAFT))
                    .thenReturn(Optional.of(existingProfile));
            when(parentProfileRepository.save(any(ParentProfile.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(parentDiseaseRepository.saveAll(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            parentProfileService.saveDraft(1L, request);

            // then
            verify(parentDiseaseRepository).deleteByParentProfile(existingProfile);
            verify(parentDiseaseRepository).saveAll(any());

            ArgumentCaptor<ParentProfile> profileCaptor = ArgumentCaptor.forClass(ParentProfile.class);
            verify(parentProfileRepository, times(1)).save(profileCaptor.capture());
            assertThat(profileCaptor.getValue()).isSameAs(existingProfile);
            assertThat(existingProfile.getName()).isEqualTo("김철수");
            assertThat(existingProfile.getMobilityLevel()).isEqualTo(MobilityLevel.SHORT_WALK);
        }

        @Test
        @DisplayName("존재하지 않는 사용자면 USER_NOT_FOUND 예외를 던진다")
        void saveDraft_throwsWhenUserNotFound() {
            // given
            setUpChildAndRequest();
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> parentProfileService.saveDraft(1L, request))
                    .isInstanceOf(GachiException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(parentProfileRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("complete")
    class Complete {

        @Test
        @DisplayName("기존 DRAFT 프로필이 없으면 신규로 생성하고 COMPLETED 상태로 저장한다")
        void complete_createsNewProfileWhenNoDraftExists() {
            // given
            setUpChildAndRequest();
            when(userRepository.findById(1L)).thenReturn(Optional.of(child));
            when(parentProfileRepository.findFirstByChildAndStatusOrderByIdDesc(child, ProfileStatus.DRAFT))
                    .thenReturn(Optional.empty());
            when(parentProfileRepository.save(any(ParentProfile.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(parentDiseaseRepository.saveAll(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ParentProfileResponse response = parentProfileService.complete(1L, request);

            // then
            ArgumentCaptor<ParentProfile> profileCaptor = ArgumentCaptor.forClass(ParentProfile.class);
            verify(parentProfileRepository).save(profileCaptor.capture());
            assertThat(profileCaptor.getValue().getStatus()).isEqualTo(ProfileStatus.COMPLETED);
            assertThat(response.status()).isEqualTo(ProfileStatus.COMPLETED);
        }

        @Test
        @DisplayName("기존 DRAFT 프로필이 있으면 재사용하여 COMPLETED 상태로 갱신한다")
        void complete_updatesExistingDraftToCompleted() {
            // given
            setUpChildAndRequest();
            ParentProfile existingProfile = ParentProfile.builder()
                    .id(50L)
                    .child(child)
                    .parent(null)
                    .name("이전이름")
                    .mobilityLevel(MobilityLevel.NORMAL)
                    .status(ProfileStatus.DRAFT)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(child));
            when(parentProfileRepository.findFirstByChildAndStatusOrderByIdDesc(child, ProfileStatus.DRAFT))
                    .thenReturn(Optional.of(existingProfile));
            when(parentProfileRepository.save(any(ParentProfile.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(parentDiseaseRepository.saveAll(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ParentProfileResponse response = parentProfileService.complete(1L, request);

            // then
            ArgumentCaptor<ParentProfile> profileCaptor = ArgumentCaptor.forClass(ParentProfile.class);
            verify(parentProfileRepository, times(1)).save(profileCaptor.capture());
            assertThat(profileCaptor.getValue()).isSameAs(existingProfile);
            assertThat(existingProfile.getStatus()).isEqualTo(ProfileStatus.COMPLETED);
            assertThat(response.status()).isEqualTo(ProfileStatus.COMPLETED);
        }

        @Test
        @DisplayName("존재하지 않는 사용자면 USER_NOT_FOUND 예외를 던진다")
        void complete_throwsWhenUserNotFound() {
            // given
            setUpChildAndRequest();
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> parentProfileService.complete(1L, request))
                    .isInstanceOf(GachiException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getDraft")
    class GetDraft {

        @Test
        @DisplayName("DRAFT 프로필과 질환 목록을 조회하여 반환한다")
        void getDraft_success() {
            // given
            setUpChildAndRequest();
            ParentProfile draftProfile = ParentProfile.builder()
                    .id(70L)
                    .child(child)
                    .parent(null)
                    .name("김철수")
                    .mobilityLevel(MobilityLevel.SHORT_WALK)
                    .status(ProfileStatus.DRAFT)
                    .build();
            ParentDisease disease = ParentDisease.builder()
                    .id(200L)
                    .parentProfile(draftProfile)
                    .category(DiseaseCategory.CHRONIC)
                    .diseaseName("고혈압")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(child));
            when(parentProfileRepository.findFirstByChildAndStatusOrderByIdDesc(child, ProfileStatus.DRAFT))
                    .thenReturn(Optional.of(draftProfile));
            when(parentDiseaseRepository.findByParentProfile(draftProfile)).thenReturn(List.of(disease));

            // when
            ParentProfileResponse response = parentProfileService.getDraft(1L);

            // then
            assertThat(response.id()).isEqualTo(70L);
            assertThat(response.name()).isEqualTo("김철수");
            assertThat(response.mobilityLevel()).isEqualTo(MobilityLevel.SHORT_WALK);
            assertThat(response.status()).isEqualTo(ProfileStatus.DRAFT);
            assertThat(response.diseases()).hasSize(1);
            assertThat(response.diseases().get(0).category()).isEqualTo(DiseaseCategory.CHRONIC);
            assertThat(response.diseases().get(0).diseaseName()).isEqualTo("고혈압");
            assertThat(response.parentUserId()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 사용자면 USER_NOT_FOUND 예외를 던진다")
        void getDraft_throwsWhenUserNotFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> parentProfileService.getDraft(1L))
                    .isInstanceOf(GachiException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("DRAFT 프로필이 없으면 PROFILE_NOT_FOUND 예외를 던진다")
        void getDraft_throwsWhenProfileNotFound() {
            // given
            setUpChildAndRequest();
            when(userRepository.findById(1L)).thenReturn(Optional.of(child));
            when(parentProfileRepository.findFirstByChildAndStatusOrderByIdDesc(child, ProfileStatus.DRAFT))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> parentProfileService.getDraft(1L))
                    .isInstanceOf(GachiException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.PROFILE_NOT_FOUND);
        }
    }
}
