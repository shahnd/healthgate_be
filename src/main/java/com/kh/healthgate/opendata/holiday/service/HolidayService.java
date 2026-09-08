package com.kh.healthgate.opendata.holiday.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.kh.healthgate.opendata.holiday.client.HolidayApiClient;
import com.kh.healthgate.opendata.holiday.client.dto.RestDeInfoRequest;
import com.kh.healthgate.opendata.holiday.client.dto.RestDeInfoResponse;
import com.kh.healthgate.opendata.holiday.dto.HolidayResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayApiClient client;

    public List<HolidayResponse> getHolidays(int year) {
        RestDeInfoResponse response = client.getRestDeInfo(new RestDeInfoRequest(String.valueOf(year), 100));

        return response.response().body().items().item().stream()
                .map(item -> new HolidayResponse(item.locdate(), item.dateName()))
                .toList();
    }
}
