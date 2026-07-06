package com.gachi.server.domain.travel.controller;

import com.gachi.server.domain.travel.service.TravelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Travel", description = "여행 API")
@RestController
@RequestMapping("/api/travels")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;
}
