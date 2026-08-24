package com.kh.healthgate.opendata.weather.model.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.opendata.weather.model.vo.WeatherForecast;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastLocation;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    Optional<WeatherForecast> findByForecastAtAndLocation(LocalDateTime forecastAt, WeatherForecastLocation location);

    boolean existsByForecastAtAndLocation(LocalDateTime forecastAt, WeatherForecastLocation location);

    List<WeatherForecast> findByForecastAtBetweenAndLocation(
            LocalDateTime start,
            LocalDateTime end,
            WeatherForecastLocation location);

    boolean existsByForecastAtBetweenAndLocation(
            LocalDateTime start,
            LocalDateTime end,
            WeatherForecastLocation location);
}
