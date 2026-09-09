package com.kh.healthgate.opendata.weather.exception;

import com.kh.healthgate.common.exception.ProblemException;

public class WeatherForecastException extends ProblemException {
    public WeatherForecastException(String message) {
        super(WeatherProblem.WEATHER_FORECAST_UNAVAILABLE, message);
    }
}
