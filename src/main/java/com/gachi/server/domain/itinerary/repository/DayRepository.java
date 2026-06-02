package com.gachi.server.domain.itinerary.repository;

import com.gachi.server.domain.itinerary.entity.Day;
import com.gachi.server.domain.travel.entity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DayRepository extends JpaRepository<Day, Long> {

    List<Day> findByTravelOrderByDayNumber(Travel travel);
}
