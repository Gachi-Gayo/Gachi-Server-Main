package com.gachi.server.infra.kakaomap;

import com.gachi.server.domain.itinerary.entity.Route;
import com.gachi.server.domain.itinerary.entity.TransportType;
import com.gachi.server.domain.itinerary.repository.RouteRepository;
import com.gachi.server.domain.place.entity.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteRefreshService {

    private final KakaoMapClient kakaoMapClient;
    private final RouteRepository routeRepository;

    @Async
    @Transactional
    public void refresh(Long routeId) {
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) {
            return;
        }

        if (route.getTransportType() != TransportType.CAR) {
            log.debug("Route {} 갱신 스킵: {} 이동 수단 미지원", routeId, route.getTransportType());
            return;
        }

        Place start = route.getStartBlock().getPlace();
        Place end = route.getEndBlock().getPlace();

        if (start == null || end == null
                || start.getLatitude() == null || start.getLongitude() == null
                || end.getLatitude() == null || end.getLongitude() == null) {
            log.debug("Route {} 갱신 스킵: 좌표 없음", routeId);
            return;
        }

        kakaoMapClient.getCarRoute(
                start.getLongitude(), start.getLatitude(),
                end.getLongitude(), end.getLatitude()
        ).ifPresent(summary -> {
            route.update(
                    summary.getDuration(),
                    summary.getDistance(),
                    summary.getFare().getToll(),
                    summary.getFare().getTaxi()
            );
            log.debug("Route {} 갱신 완료: {}m, {}초", routeId, summary.getDistance(), summary.getDuration());
        });
    }
}
