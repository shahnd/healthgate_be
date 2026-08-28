package com.kh.healthgate.opendata.weather.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class VilageFcstDateTimeFormatter {
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddkkmm");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("kkmm");

    public static LocalDateTime toLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, dateTimeFormatter);
    }

    public static LocalDateTime toLocalDateTime(String date, String time) {
        return toLocalDateTime(date + time);
    }

    public static LocalDate toLocalDate(String date) {
        return LocalDate.parse(date, dateFormatter);
    }

    public static LocalTime toLocalTime(String time) {
        return LocalTime.parse(time, timeFormatter);
    }

    public static String toDateTimeString(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }

    public static String toDateString(LocalDate date) {
        return date.format(dateFormatter);
    }

    public static String toTimeString(LocalTime time) {
        return time.format(timeFormatter);
    }
}
