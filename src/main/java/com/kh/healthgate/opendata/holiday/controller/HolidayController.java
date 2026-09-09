package com.kh.healthgate.opendata.holiday.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.kh.healthgate.opendata.holiday.dto.HolidayResponse;
import com.kh.healthgate.opendata.holiday.service.HolidayService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("holidays")
@RequiredArgsConstructor
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping
    public List<HolidayResponse> getHolidays(@RequestParam int year) {
        return holidayService.getHolidays(year);
    }
}
