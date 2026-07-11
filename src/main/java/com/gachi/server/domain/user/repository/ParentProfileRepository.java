package com.gachi.server.domain.user.repository;

import com.gachi.server.domain.user.entity.ParentProfile;
import com.gachi.server.domain.user.entity.ProfileStatus;
import com.gachi.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParentProfileRepository extends JpaRepository<ParentProfile, Long> {

    Optional<ParentProfile> findFirstByChildAndStatusOrderByIdDesc(User child, ProfileStatus status);

    List<ParentProfile> findByChildAndParentIsNull(User child);
}
