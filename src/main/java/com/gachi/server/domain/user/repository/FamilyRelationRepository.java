package com.gachi.server.domain.user.repository;

import com.gachi.server.domain.user.entity.FamilyRelation;
import com.gachi.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyRelationRepository extends JpaRepository<FamilyRelation, Long> {

    Optional<FamilyRelation> findByChild(User child);
}
