package com.kh.healthgate.opendata.weather.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VilageFcstDateTimeUtilsTest {
    @ParameterizedTest
    @CsvSource({
            "2026-09-14T00:00:00, 2026-09-13T23:00:00",
            "2026-09-14T00:05:00, 2026-09-13T23:00:00",
            "2026-09-14T02:09:59, 2026-09-13T23:00:00",
            "2026-09-14T02:10:00, 2026-09-14T02:00:00",
            "2026-09-14T05:09:59, 2026-09-14T02:00:00",
            "2026-09-14T05:10:00, 2026-09-14T05:00:00",
            "2026-09-14T07:00:00, 2026-09-14T05:00:00",
            "2026-09-14T08:00:00, 2026-09-14T05:00:00",
            "2026-09-14T08:09:59, 2026-09-14T05:00:00",
            "2026-09-14T08:10:00, 2026-09-14T08:00:00",
            "2026-09-14T15:00:00, 2026-09-14T08:00:00",
            "2026-09-14T23:59:59, 2026-09-14T08:00:00",
            "2026-01-01T00:05:00, 2025-12-31T23:00:00",
            "2026-03-01T02:09:59, 2026-02-28T23:00:00"
    })
    void prefersTodayT3WhenAvailableOtherwiseLatestAvailableBaseTime(String now, String expected) {
        assertThat(VilageFcstDateTimeUtils.preferredBaseDateTime(LocalDateTime.parse(now)))
                .isEqualTo(LocalDateTime.parse(expected));
    }
}
