package com.gachi.server.domain.user.repository;

import com.gachi.server.domain.user.entity.ParentDisease;
import com.gachi.server.domain.user.entity.ParentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentDiseaseRepository extends JpaRepository<ParentDisease, Long> {

    List<ParentDisease> findByParentProfile(ParentProfile parentProfile);

    void deleteByParentProfile(ParentProfile parentProfile);
}
