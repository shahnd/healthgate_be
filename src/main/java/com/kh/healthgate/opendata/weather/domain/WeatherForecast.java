package com.kh.healthgate.opendata.weather.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "weather_forecasts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_weather_forecasts_forecast_location",
                columnNames = { "forecast_at", "location" }))
@Getter
@ToString
@NoArgsConstructor
public class WeatherForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime forecastAt;

    @Column(nullable = false)
    private BigDecimal temperature;

    @Column(nullable = false)
    private BigDecimal humidity;

    @Column(nullable = false)
    private BigDecimal precipitationProbability;

    @Column(nullable = false)
    private String precipitation;

    @Column(nullable = false)
    private String snowfall;

    @Column(nullable = false)
    private BigDecimal windSpeed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeatherForecastPrecipitationType precipitationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeatherForecastSkyCondition skyCondition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeatherForecastLocation location;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public WeatherForecast(
            LocalDateTime forecastAt,
            BigDecimal temperature,
            BigDecimal humidity,
            BigDecimal precipitationProbability,
            String precipitation,
            String snowfall,
            BigDecimal windSpeed,
            WeatherForecastPrecipitationType precipitationType,
            WeatherForecastSkyCondition skyCondition,
            WeatherForecastLocation location) {
        this.forecastAt = forecastAt;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitationProbability = precipitationProbability;
        this.precipitation = precipitation;
        this.snowfall = snowfall;
        this.windSpeed = windSpeed;
        this.precipitationType = precipitationType;
        this.skyCondition = skyCondition;
        this.location = location;
    }

    public void updateFrom(WeatherForecast forecast) {
        this.temperature = forecast.temperature;
        this.humidity = forecast.humidity;
        this.precipitationProbability = forecast.precipitationProbability;
        this.precipitation = forecast.precipitation;
        this.snowfall = forecast.snowfall;
        this.windSpeed = forecast.windSpeed;
        this.precipitationType = forecast.precipitationType;
        this.skyCondition = forecast.skyCondition;
    }

    public boolean hasPrecipitation() {
        return hasWeatherAmount(precipitation, "강수없음");
    }

    public boolean hasSnowfall() {
        return hasWeatherAmount(snowfall, "적설없음");
    }

    private static boolean hasWeatherAmount(String amount, String noAmountValue) {
        return amount != null
                && !amount.isBlank()
                && !amount.equals("-")
                && !amount.equals("0")
                && !amount.equals(noAmountValue);
    }
}
