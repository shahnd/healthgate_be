package com.kh.healthgate.safety.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.kh.healthgate.common.exception.ApiExceptionHandler;
import com.kh.healthgate.common.exception.ProblemException;
import com.kh.healthgate.opendata.weather.exception.WeatherApiException;
import com.kh.healthgate.opendata.weather.exception.WeatherForecastException;
import com.kh.healthgate.safety.controller.SafetyBriefingController;
import com.kh.healthgate.safety.service.SafetyBriefingService;

@ExtendWith(MockitoExtension.class)
class SafetyExceptionHandlerTest {

    @Mock
    private SafetyBriefingService safetyBriefingService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SafetyBriefingController(safetyBriefingService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void handlesSafetyBriefingGenerationExceptionAsProblemDetail() throws Exception {
        // given
        when(safetyBriefingService.getTodayBriefing())
                .thenThrow(new SafetyBriefingGenerationException(new RuntimeException("AI error")));

        // when, then
        mockMvc.perform(get("/safety-briefings/today"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("/problems/safety-briefing-generation-failed"))
                .andExpect(jsonPath("$.title").value("안전 브리핑 생성 실패"))
                .andExpect(jsonPath("$.detail").value("오늘의 안전 브리핑을 생성하지 못했습니다."))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.code").value("SAFETY_BRIEFING_GENERATION_FAILED"));
    }
    @ParameterizedTest
    @MethodSource("weatherExceptions")
    void handlesWeatherExceptionsAsProblemDetail(ProblemException exception) throws Exception {
        when(safetyBriefingService.getTodayBriefing()).thenThrow(exception);

        mockMvc.perform(get("/safety-briefings/today"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("/problems/weather-forecast-unavailable"))
                .andExpect(jsonPath("$.title").value("기상예보 조회 실패"))
                .andExpect(jsonPath("$.detail").value("오늘 업무시간의 기상예보를 불러오지 못했습니다."))
                .andExpect(jsonPath("$.code").value("WEATHER_FORECAST_UNAVAILABLE"));
    }

    private static java.util.stream.Stream<ProblemException> weatherExceptions() {
        return java.util.stream.Stream.of(
                new WeatherApiException("외부 API 오류"),
                new WeatherForecastException("예보 데이터 오류"));
    }
}
