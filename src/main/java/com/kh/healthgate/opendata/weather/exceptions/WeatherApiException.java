package com.kh.healthgate.opendata.weather.exceptions;

public class WeatherApiException extends RuntimeException {
    public WeatherApiException(String message) {
        super(message);
    }
}
