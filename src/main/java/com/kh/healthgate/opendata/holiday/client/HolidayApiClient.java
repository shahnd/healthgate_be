package com.kh.healthgate.opendata.holiday.client;

import java.nio.charset.StandardCharsets;

import org.json.XML;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.healthgate.opendata.config.OpenDataProperties;
import com.kh.healthgate.opendata.holiday.client.dto.RestDeInfoRequest;
import com.kh.healthgate.opendata.holiday.client.dto.RestDeInfoResponse;
import com.kh.healthgate.opendata.holiday.exception.HolidayApiException;

@Component
public class HolidayApiClient {

    private final OpenDataProperties properties;
    private final ObjectMapper mapper;
    private final RestClient restClient;

    public HolidayApiClient(OpenDataProperties properties, ObjectMapper mapper) {
        this.properties = properties;
        this.mapper = mapper;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public RestDeInfoResponse getRestDeInfo(RestDeInfoRequest request) {

        byte[] body = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
                        .queryParam("ServiceKey", properties.getServiceKey())
                        .queryParam("solYear", request.solYear())
                        .queryParam("numOfRows", request.numOfRows())
                        .queryParam("pageNo", 1)
                        .build())
                .retrieve()
                .body(byte[].class);

        String raw = new String(body, StandardCharsets.UTF_8);

        try {
            String json = isXml(raw) ? XML.toJSONObject(raw).toString() : raw;
            RestDeInfoResponse response = mapper.readValue(json, RestDeInfoResponse.class);

            if (!"00".equals(response.response().header().resultCode())) {
                throw new HolidayApiException(
                        "공휴일 정보를 불러오지 못했습니다: " + response.response().header().resultCode()
                                + ": " + response.response().header().resultMsg());
            }

            return response;
        } catch (HolidayApiException e) {
            throw e;
        } catch (Exception e) {
            throw new HolidayApiException("공휴일 응답 파싱에 실패했습니다: " + e.getMessage());
        }
    }

    private boolean isXml(String text) {
        return text != null && text.trim().startsWith("<");
    }
}
