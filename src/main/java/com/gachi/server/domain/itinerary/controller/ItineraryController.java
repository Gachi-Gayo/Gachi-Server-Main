package com.gachi.server.domain.itinerary.controller;

import com.gachi.server.domain.itinerary.service.ItineraryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Itinerary", description = "일정 API")
@RestController
@RequestMapping("/api/travels/{travelId}/itinerary")
@RequiredArgsConstructor
public class ItineraryController {

    private final ItineraryService itineraryService;
}
