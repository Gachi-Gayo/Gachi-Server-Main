package com.gachi.server.domain.travel.repository;

import com.gachi.server.domain.travel.entity.Travel;
import com.gachi.server.domain.travel.entity.TravelMember;
import com.gachi.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelMemberRepository extends JpaRepository<TravelMember, Long> {

    List<TravelMember> findByTravel(Travel travel);

    List<TravelMember> findByUser(User user);

    Optional<TravelMember> findByTravelAndUser(Travel travel, User user);

    boolean existsByTravelAndUser(Travel travel, User user);
}
