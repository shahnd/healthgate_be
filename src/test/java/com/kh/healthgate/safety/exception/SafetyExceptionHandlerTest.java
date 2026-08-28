package com.kh.healthgate.safety.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.kh.healthgate.common.exception.ApiExceptionHandler;
import com.kh.healthgate.safety.controller.SafetyBriefingController;
import com.kh.healthgate.safety.model.service.SafetyBriefingService;

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
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.code").value("SAFETY_BRIEFING_GENERATION_FAILED"));
    }
}
