package com.kh.healthgate.weather.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.kh.healthgate.opendata.config.OpendataProperties;

@Component
public class WeatherApiClient {
    private final OpendataProperties properties;
    private final RestClient restClient;

    public WeatherApiClient(OpendataProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().baseUrl(properties.getBaseUrl()).build();
    }

    public VilageFcstResponse getVilageFcst(VilageFcstRequest request) {
        VilageFcstResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/1360000/VilageFcstInfoService_2.0/getVilageFcst")
                        .queryParam("ServiceKey", properties.getServiceKey())
                        .queryParam("pageNo", request.pageNo())
                        .queryParam("numOfRows", request.numOfRows())
                        .queryParam("dataType", request.dataType())
                        .queryParam("base_date", request.baseDate())
                        .queryParam("base_time", request.baseTime())
                        .queryParam("nx", request.nx())
                        .queryParam("ny", request.ny())
                        .build())
                .retrieve()
                .body(VilageFcstResponse.class);

        return response;
    }

}
