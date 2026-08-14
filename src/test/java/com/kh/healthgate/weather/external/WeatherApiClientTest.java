package com.kh.healthgate.weather.external;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.EnableWireMock;

@EnableWireMock
@SpringBootTest(classes = WeatherApiClientTest.AppConfiguration.class)
class WeatherApiClientTest {
    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    @Test
    void getsVilageFcst() {
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
        WeatherApiClient weatherApiClient = new WeatherApiClient(wireMockUrl);

        // when
        VilageFcstResponse response = weatherApiClient.getVilageFcst(
                new VilageFcstRequest(
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
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
                .isEqualTo(VilageFcstResponse.Category.TEMPERATURE);

        assertThat(response.response()
                .header()
                .resultCode())
                .isEqualTo(VilageFcstResponse.ResultCode.NORMAL_SERVICE);
    }

    @SpringBootApplication
    static class AppConfiguration {
    }
}