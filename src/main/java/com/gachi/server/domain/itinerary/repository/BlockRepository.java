package com.gachi.server.domain.itinerary.repository;

import com.gachi.server.domain.itinerary.entity.Block;
import com.gachi.server.domain.itinerary.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {

    List<Block> findByDayOrderBySequence(Day day);
}
