package com.kh.healthgate.opendata.weather.exception;

import com.kh.healthgate.common.exception.ProblemException;
import com.kh.healthgate.common.exception.ProblemType;

public class WeatherForecastException extends ProblemException {
    public WeatherForecastException(String message) {
        super(ProblemType.WEATHER_FORECAST_UNAVAILABLE, message);
    }
}
