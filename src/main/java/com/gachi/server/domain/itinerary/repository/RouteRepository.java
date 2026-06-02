package com.gachi.server.domain.itinerary.repository;

import com.gachi.server.domain.itinerary.entity.Block;
import com.gachi.server.domain.itinerary.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByStartBlockAndEndBlock(Block startBlock, Block endBlock);
}
