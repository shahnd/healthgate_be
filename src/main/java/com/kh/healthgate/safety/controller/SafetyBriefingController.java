package com.kh.healthgate.safety.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.safety.dto.SafetyBriefingResponse;
import com.kh.healthgate.safety.service.SafetyBriefingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/safety-briefings")
@RequiredArgsConstructor
public class SafetyBriefingController {
    private final SafetyBriefingService safetyBriefingService;

    @GetMapping("/today")
    public SafetyBriefingResponse today() {
        return safetyBriefingService.getTodayBriefing();
    }
}
