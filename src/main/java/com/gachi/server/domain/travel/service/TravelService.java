package com.gachi.server.domain.travel.service;

import com.gachi.server.domain.travel.repository.TravelMemberRepository;
import com.gachi.server.domain.travel.repository.TravelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {

    private final TravelRepository travelRepository;
    private final TravelMemberRepository travelMemberRepository;
}
