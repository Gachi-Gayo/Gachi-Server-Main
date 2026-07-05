package com.gachi.server.infra.kakaomap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoRouteResponse {

    private List<Route> routes;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {

        @JsonProperty("result_code")
        private int resultCode;

        @JsonProperty("result_msg")
        private String resultMsg;

        private Summary summary;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary {

        private int distance;
        private int duration;
        private Fare fare;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fare {

        private int taxi;
        private int toll;
    }
}
