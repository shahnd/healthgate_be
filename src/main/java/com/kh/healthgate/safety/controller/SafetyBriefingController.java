package com.kh.healthgate.safety.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.common.exception.ApiProblemResponse;
import com.kh.healthgate.common.exception.AuthenticationErrorResponse;
import com.kh.healthgate.safety.dto.SafetyBriefingResponse;
import com.kh.healthgate.safety.service.SafetyBriefingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "안전 브리핑", description = "날씨와 활성 안전문서를 반영한 일일 안전 브리핑 API")
@RestController
@RequestMapping(value = "/safety-briefings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SafetyBriefingController {
    private static final String AUTHENTICATION_ERROR_EXAMPLE =
            "{\"message\":\"인증 토큰이 누락되었습니다.\"}";
    private static final String BRIEFING_GENERATION_ERROR_EXAMPLE = """
            {
              "type": "/problems/safety-briefing-generation-failed",
              "title": "안전 브리핑 생성 실패",
              "status": 503,
              "detail": "오늘의 안전 브리핑을 생성하지 못했습니다.",
              "instance": "/healthgate/safety-briefings/today",
              "code": "SAFETY_BRIEFING_GENERATION_FAILED"
            }
            """;
    private static final String WEATHER_ERROR_EXAMPLE = """
            {
              "type": "/problems/weather-forecast-unavailable",
              "title": "기상예보 조회 실패",
              "status": 503,
              "detail": "오늘 업무시간의 기상예보를 불러오지 못했습니다.",
              "instance": "/healthgate/safety-briefings/today",
              "code": "WEATHER_FORECAST_UNAVAILABLE"
            }
            """;
    private static final String INTERNAL_SERVER_ERROR_EXAMPLE = """
            {
              "type": "/problems/internal-server-error",
              "title": "서버 내부 오류",
              "status": 500,
              "detail": "요청을 처리하는 중 오류가 발생했습니다.",
              "instance": "/healthgate/safety-briefings/today",
              "code": "INTERNAL_SERVER_ERROR"
            }
            """;

    private final SafetyBriefingService safetyBriefingService;

    @Operation(
            summary = "오늘의 안전 브리핑 조회",
            description = "오늘의 날씨와 활성화된 인덱싱 완료 문서를 반영한 브리핑을 조회합니다. "
                    + "동일한 입력으로 생성된 브리핑이 있으면 저장된 결과를 반환합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "오늘의 안전 브리핑 조회 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(
                    responseCode = "503",
                    description = "기상예보를 조회할 수 없거나 안전 브리핑 생성에 실패함",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "안전 브리핑 생성 실패",
                                            value = BRIEFING_GENERATION_ERROR_EXAMPLE),
                                    @ExampleObject(
                                            name = "기상예보 조회 실패",
                                            value = WEATHER_ERROR_EXAMPLE)
                            })),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @GetMapping("/today")
    public SafetyBriefingResponse today() {
        return safetyBriefingService.getTodayBriefing();
    }
}
