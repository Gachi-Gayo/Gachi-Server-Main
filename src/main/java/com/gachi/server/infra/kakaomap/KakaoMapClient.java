package com.gachi.server.infra.kakaomap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class KakaoMapClient {

    private final RestClient restClient;

    public KakaoMapClient(@Value("${kakao.rest-api-key}") String restApiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://apis-navi.kakaomobility.com")
                .defaultHeader("Authorization", "KakaoAK " + restApiKey)
                .build();
    }

    public Optional<KakaoRouteResponse.Summary> getCarRoute(double originLng, double originLat, double destLng, double destLat) {
        try {
            KakaoRouteResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/directions")
                            .queryParam("origin", originLng + "," + originLat)
                            .queryParam("destination", destLng + "," + destLat)
                            .queryParam("priority", "RECOMMEND")
                            .build())
                    .retrieve()
                    .body(KakaoRouteResponse.class);

            if (response == null) {
                return Optional.empty();
            }

            List<KakaoRouteResponse.Route> routes = response.getRoutes();
            if (routes == null || routes.isEmpty()) {
                return Optional.empty();
            }

            KakaoRouteResponse.Route route = routes.get(0);
            if (route.getResultCode() != 0) {
                log.warn("카카오 길찾기 API 오류: code={}, msg={}", route.getResultCode(), route.getResultMsg());
                return Optional.empty();
            }

            return Optional.ofNullable(route.getSummary());

        } catch (Exception e) {
            log.error("카카오 길찾기 API 호출 실패: origin=({},{}), dest=({},{})", originLng, originLat, destLng, destLat, e);
            return Optional.empty();
        }
    }
}
