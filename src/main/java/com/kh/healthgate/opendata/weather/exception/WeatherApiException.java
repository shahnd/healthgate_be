package com.kh.healthgate.opendata.weather.exception;

import com.kh.healthgate.common.exception.ProblemException;
import com.kh.healthgate.common.exception.ProblemType;

public class WeatherApiException extends ProblemException {
    public WeatherApiException(String message) {
        super(ProblemType.WEATHER_FORECAST_UNAVAILABLE, message);
    }
}
