package com.gachi.server.domain.user.service;

import com.gachi.server.domain.user.dto.ChildOnboardResponse;
import com.gachi.server.domain.user.dto.ParentOnboardRequest;
import com.gachi.server.domain.user.entity.FamilyRelation;
import com.gachi.server.domain.user.entity.ParentProfile;
import com.gachi.server.domain.user.entity.User;
import com.gachi.server.domain.user.entity.UserRole;
import com.gachi.server.domain.user.repository.FamilyRelationRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FamilyRelationRepository familyRelationRepository;

    @Mock
    private ParentProfileRepository parentProfileRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("childOnboard")
    class ChildOnboard {

        @Test
        @DisplayName("정상적으로 자녀 역할과 8자리 초대 코드를 발급한다")
        void childOnboard_success() {
            // given
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .nickname("자녀")
                    .build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ChildOnboardResponse response = userService.childOnboard(userId);

            // then
            assertThat(response.inviteCode()).matches("\\d{8}");

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User savedUser = captor.getValue();
            assertThat(savedUser.getRole()).isEqualTo(UserRole.CHILD);
            assertThat(savedUser.getInviteCode()).matches("\\d{8}");
        }

        @Test
        @DisplayName("존재하지 않는 사용자면 USER_NOT_FOUND 예외를 던진다")
        void childOnboard_throwsWhenUserNotFound() {
            // given
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.childOnboard(userId))
                    .isInstanceOf(GachiException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("초대 코드가 중복되면 재시도하여 고유한 코드를 발급한다")
        void childOnboard_retriesOnInviteCodeCollision() {
            // given
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .nickname("자녀")
                    .build();
            User otherUser = User.builder()
                    .id(2L)
                    .nickname("다른 사용자")
                    .inviteCode("11111111")
                    .build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.findByInviteCode(anyString()))
                    .thenReturn(Optional.of(otherUser))
                    .thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ChildOnboardResponse response = userService.childOnboard(userId);

            // then
            assertThat(response.inviteCode()).matches("\\d{8}");
            verify(userRepository, atLeast(2)).findByInviteCode(anyString());
        }
    }

    @Nested
    @DisplayName("parentOnboard")
    class ParentOnboard {

        @Test
        @DisplayName("정상적으로 부모-자녀를 연동하고 미연동 프로필에 부모를 연결한다")
        void parentOnboard_success() {
            // given
            Long parentUserId = 10L;
            User parentUser = User.builder()
                    .id(parentUserId)
                    .nickname("부모")
                    .build();
            User childUser = User.builder()
                    .id(20L)
                    .nickname("자녀")
                    .inviteCode("48213097")
                    .build();
            ParentOnboardRequest request = new ParentOnboardRequest("48213097");

            ParentProfile unlinkedProfile = ParentProfile.builder()
                    .id(100L)
                    .child(childUser)
                    .parent(null)
                    .name("김철수")
                    .build();

            when(userRepository.findById(parentUserId)).thenReturn(Optional.of(parentUser));
            when(userRepository.findByInviteCode("48213097")).thenReturn(Optional.of(childUser));
            when(familyRelationRepository.findByChild(childUser)).thenReturn(Optional.empty());
            when(parentProfileRepository.findByChildAndParentIsNull(childUser))
                    .thenReturn(List.of(unlinkedProfile));

            // when
            userService.parentOnboard(parentUserId, request);

            // then
            ArgumentCaptor<FamilyRelation> captor = ArgumentCaptor.forClass(FamilyRelation.class);
            verify(familyRelationRepository).save(captor.capture());
            FamilyRelation savedRelation = captor.getValue();
            assertThat(savedRelation.getParent()).isEqualTo(parentUser);
            assertThat(savedRelation.getChild()).isEqualTo(childUser);

            assertThat(unlinkedProfile.getParent()).isEqualTo(parentUser);
        }

        @Test
        @DisplayName("부모 사용자가 존재하지 않으면 USER_NOT_FOUND 예외를 던진다")
        void parentOnboard_throwsWhenUserNotFound() {
            // given
            Long parentUserId = 999L;
            ParentOnboardRequest request = new ParentOnboardRequest("48213097");
            when(userRepository.findById(parentUserId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.parentOnboard(parentUserId, request))
                    .isInstanceOf(GachiException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("초대 코드가 유효하지 않으면 INVALID_INVITE_CODE 예외를 던진다")
        void parentOnboard_throwsWhenInvalidInviteCode() {
            // given
            Long parentUserId = 10L;
            User parentUser = User.builder()
                    .id(parentUserId)
                    .nickname("부모")
                    .build();
            ParentOnboardRequest request = new ParentOnboardRequest("00000000");

            when(userRepository.findById(parentUserId)).thenReturn(Optional.of(parentUser));
            when(userRepository.findByInviteCode("00000000")).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.parentOnboard(parentUserId, request))
                    .isInstanceOf(GachiException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_INVITE_CODE);

            verify(familyRelationRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미 다른 부모와 연결된 자녀면 ALREADY_LINKED 예외를 던진다")
        void parentOnboard_throwsWhenAlreadyLinked() {
            // given
            Long parentUserId = 10L;
            User parentUser = User.builder()
                    .id(parentUserId)
                    .nickname("부모")
                    .build();
            User childUser = User.builder()
                    .id(20L)
                    .nickname("자녀")
                    .inviteCode("48213097")
                    .build();
            User anotherParent = User.builder()
                    .id(30L)
                    .nickname("다른 부모")
                    .build();
            ParentOnboardRequest request = new ParentOnboardRequest("48213097");

            FamilyRelation existingRelation = FamilyRelation.builder()
                    .id(1L)
                    .parent(anotherParent)
                    .child(childUser)
                    .build();

            when(userRepository.findById(parentUserId)).thenReturn(Optional.of(parentUser));
            when(userRepository.findByInviteCode("48213097")).thenReturn(Optional.of(childUser));
            when(familyRelationRepository.findByChild(childUser)).thenReturn(Optional.of(existingRelation));

            // when & then
            assertThatThrownBy(() -> userService.parentOnboard(parentUserId, request))
                    .isInstanceOf(GachiException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ALREADY_LINKED);

            verify(familyRelationRepository, times(1)).findByChild(childUser);
            verify(familyRelationRepository, never()).save(any());
        }
    }
}
