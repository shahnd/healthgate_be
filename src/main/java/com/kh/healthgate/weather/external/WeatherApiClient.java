package com.kh.healthgate.weather.external;

import org.springframework.web.client.RestClient;

public class WeatherApiClient {
    private final String baseUrl = "https://apis.data.go.kr";
    private final RestClient restClient;

    public WeatherApiClient() {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public WeatherApiClient(String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public VilageFcstResponse getVilageFcst(VilageFcstRequest request) {
        VilageFcstResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/1360000/VilageFcstInfoService_2.0/getVilageFcst")
                        .queryParam("ServiceKey", request.serviceKey())
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
