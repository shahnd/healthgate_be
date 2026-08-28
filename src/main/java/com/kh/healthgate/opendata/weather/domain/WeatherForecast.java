package com.kh.healthgate.opendata.weather.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
                name = "uk_weather_forecast_at_location",
                columnNames = { "forecast_at", "location" }))
@Getter
@ToString
@NoArgsConstructor
public class WeatherForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime forecastAt;

    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal precipitationProbability;
    private String precipitation;
    private String snowfall;
    private BigDecimal windSpeed;

    @Enumerated(EnumType.STRING)
    private WeatherForecastPrecipitationType precipitationType;

    @Enumerated(EnumType.STRING)
    private WeatherForecastSkyCondition skyCondition;

    @Enumerated(EnumType.STRING)
    private WeatherForecastLocation location;

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
