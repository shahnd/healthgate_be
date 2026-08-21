package com.kh.healthgate.opendata.weather.runner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.kh.healthgate.opendata.weather.model.service.WeatherService;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecast;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastLocation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherRunner implements CommandLineRunner {
    private final WeatherService weatherService;

    @Override
    public void run(String... args) throws Exception {
        WeatherForecast forecast = weatherService.getLatestForecast(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
                WeatherForecastLocation.YEOKSAM1);

        log.info(forecast.toString());
        log.info("weatherRunner done.");
    }
}
