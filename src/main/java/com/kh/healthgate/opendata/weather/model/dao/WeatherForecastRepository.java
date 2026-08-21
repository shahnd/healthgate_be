package com.kh.healthgate.opendata.weather.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.opendata.weather.model.vo.WeatherForecast;
import java.util.Optional;
import java.time.LocalDateTime;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    Optional<WeatherForecast> findByForecastAt(LocalDateTime forecastAt);

    boolean existsByForecastAt(LocalDateTime forecastAt);
}
