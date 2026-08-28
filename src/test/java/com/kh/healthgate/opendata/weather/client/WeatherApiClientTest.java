package com.kh.healthgate.opendata.weather.client;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.kh.healthgate.opendata.config.OpenDataProperties;
import com.kh.healthgate.opendata.weather.client.dto.VilageFcstCategory;
import com.kh.healthgate.opendata.weather.client.dto.VilageFcstRequest;
import com.kh.healthgate.opendata.weather.client.dto.VilageFcstResponse;
import com.kh.healthgate.opendata.weather.client.dto.VilageFcstResultCode;

@WireMockTest
class WeatherApiClientTest {
    @Test
    void getsVilageFcst(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(get(
                urlPathEqualTo("/1360000/VilageFcstInfoService_2.0/getVilageFcst"))
                .willReturn(okJson("""
                        {
                          "response": {
                            "header": {
                              "resultCode": "00",
                              "resultMsg": "NORMAL_SERVICE"
                            },
                            "body": {
                              "dataType": "JSON",
                              "items": {
                                "item": [
                                  {
                                    "baseDate": "20260813",
                                    "baseTime": "0500",
                                    "category": "TMP",
                                    "fcstDate": "20260813",
                                    "fcstTime": "0600",
                                    "fcstValue": "22",
                                    "nx": 55,
                                    "ny": 127
                                  }
                                ]
                              },
                              "pageNo": 1,
                              "numOfRows": 1,
                              "totalCount": 907
                            }
                          }
                        }
                                """)));
        String baseUrl = wmRuntimeInfo.getHttpBaseUrl();
        OpenDataProperties properties = new OpenDataProperties("xxxxxxxx", baseUrl);
        WeatherApiClient weatherApiClient = new WeatherApiClient(properties);

        // when
        VilageFcstResponse response = weatherApiClient.getVilageFcst(
                new VilageFcstRequest(
                        1,
                        1,
                        "20260813",
                        "0500",
                        55,
                        127));

        // then
        assertThat(response.response()
                .body()
                .items()
                .item())
                .hasSize(1);

        assertThat(response.response()
                .body()
                .items()
                .item()
                .getFirst()
                .fcstValue())
                .isEqualTo("22");

        assertThat(response.response()
                .body()
                .items()
                .item()
                .getFirst()
                .category())
                .isEqualTo(VilageFcstCategory.TEMPERATURE.getCode());

        assertThat(response.response()
                .header()
                .resultCode())
                .isEqualTo(VilageFcstResultCode.NORMAL_SERVICE.getCode());
    }
}
