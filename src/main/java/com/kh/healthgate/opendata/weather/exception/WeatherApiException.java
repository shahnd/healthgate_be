package com.kh.healthgate.opendata.weather.exception;

import com.kh.healthgate.common.exception.ProblemException;

public class WeatherApiException extends ProblemException {
    public WeatherApiException(String message) {
        super(WeatherProblem.WEATHER_FORECAST_UNAVAILABLE, message);
    }
}
