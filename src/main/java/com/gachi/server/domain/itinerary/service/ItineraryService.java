package com.gachi.server.domain.itinerary.service;

import com.gachi.server.domain.itinerary.repository.BlockRepository;
import com.gachi.server.domain.itinerary.repository.DayRepository;
import com.gachi.server.domain.itinerary.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItineraryService {

    private final DayRepository dayRepository;
    private final BlockRepository blockRepository;
    private final RouteRepository routeRepository;
}
