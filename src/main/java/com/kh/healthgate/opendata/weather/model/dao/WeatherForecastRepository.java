package com.kh.healthgate.opendata.weather.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.opendata.weather.model.vo.WeatherForecast;
import java.util.Optional;
import java.time.LocalDateTime;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastLocation;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    Optional<WeatherForecast> findByForecastAtAndLocation(LocalDateTime forecastAt, WeatherForecastLocation location);

    boolean existsByForecastAtAndLocation(LocalDateTime forecastAt, WeatherForecastLocation location);
}
