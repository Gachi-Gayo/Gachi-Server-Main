package com.gachi.server.domain.user.service;

import com.gachi.server.domain.user.dto.ChildOnboardResponse;
import com.gachi.server.domain.user.dto.ParentOnboardRequest;
import com.gachi.server.domain.user.entity.FamilyRelation;
import com.gachi.server.domain.user.entity.ParentProfile;
import com.gachi.server.domain.user.entity.User;
import com.gachi.server.domain.user.repository.FamilyRelationRepository;
import com.gachi.server.domain.user.repository.ParentProfileRepository;
import com.gachi.server.domain.user.repository.UserRepository;
import com.gachi.server.global.exception.ErrorCode;
import com.gachi.server.global.exception.GachiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FamilyRelationRepository familyRelationRepository;
    private final ParentProfileRepository parentProfileRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public ChildOnboardResponse childOnboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GachiException(ErrorCode.USER_NOT_FOUND));

        String inviteCode = generateUniqueInviteCode();
        user.assignChildRole(inviteCode);
        userRepository.save(user);

        return new ChildOnboardResponse(inviteCode);
    }

    @Transactional
    public void parentOnboard(Long userId, ParentOnboardRequest request) {
        User parent = userRepository.findById(userId)
                .orElseThrow(() -> new GachiException(ErrorCode.USER_NOT_FOUND));

        User child = userRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> new GachiException(ErrorCode.INVALID_INVITE_CODE));

        if (familyRelationRepository.findByChild(child).isPresent()) {
            throw new GachiException(ErrorCode.ALREADY_LINKED);
        }

        parent.assignParentRole();

        FamilyRelation familyRelation = FamilyRelation.builder()
                .parent(parent)
                .child(child)
                .build();
        familyRelationRepository.save(familyRelation);

        List<ParentProfile> unlinkedProfiles = parentProfileRepository.findByChildAndParentIsNull(child);
        for (ParentProfile profile : unlinkedProfiles) {
            profile.linkParent(parent);
        }
    }

    private String generateUniqueInviteCode() {
        String inviteCode;
        do {
            inviteCode = String.format("%08d", RANDOM.nextInt(100_000_000));
        } while (userRepository.findByInviteCode(inviteCode).isPresent());
        return inviteCode;
    }
}
