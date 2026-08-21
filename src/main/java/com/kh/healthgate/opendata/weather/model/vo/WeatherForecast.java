package com.kh.healthgate.opendata.weather.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "weather_forecasts")
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
    private BigDecimal snowfall;
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
            BigDecimal snowfall,
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

    public static WeatherForecast of(
            LocalDateTime forecastAt,
            WeatherForecastLocation location,
            Map<VilageFcstCategory, String> forecastValues) {
        String snow = forecastValues.get(VilageFcstCategory.SNOWFALL_AMOUNT);
        return new WeatherForecast(
                forecastAt,
                new BigDecimal(forecastValues.get(VilageFcstCategory.TEMPERATURE)),
                new BigDecimal(forecastValues.get(VilageFcstCategory.HUMIDITY)),
                new BigDecimal(forecastValues.get(VilageFcstCategory.PRECIPITATION_PROBABILITY)),
                forecastValues.get(VilageFcstCategory.PRECIPITATION_AMOUNT),
                new BigDecimal(snow.equals("적설없음") ? "0.0" : snow),
                new BigDecimal(forecastValues.get(VilageFcstCategory.WIND_SPEED)),
                WeatherForecastPrecipitationType.from(forecastValues.get(VilageFcstCategory.PRECIPITATION_TYPE)),
                WeatherForecastSkyCondition.from(forecastValues.get(VilageFcstCategory.SKY_CONDITION)),
                location);
    }
}
