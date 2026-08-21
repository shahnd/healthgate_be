package com.kh.healthgate.opendata.weather;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.kh.healthgate.opendata.config.OpendataProperties;
import com.kh.healthgate.opendata.weather.exceptions.WeatherApiException;
import com.kh.healthgate.opendata.weather.model.vo.VilageFcstRequest;
import com.kh.healthgate.opendata.weather.model.vo.VilageFcstResponse;
import com.kh.healthgate.opendata.weather.model.vo.VilageFcstResultCode;

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

        VilageFcstResultCode resultCode = VilageFcstResultCode.from(response.response().header().resultCode());

        if (!resultCode.equals(VilageFcstResultCode.NORMAL_SERVICE)) {
            throw new WeatherApiException(
                    "예보 정보를 불러오지 못 했습니다: " + resultCode.name() + ": " + resultCode.getDisplayName() + "\n" + request
                            + "\n" + response);
        }

        return response;
    }
}
