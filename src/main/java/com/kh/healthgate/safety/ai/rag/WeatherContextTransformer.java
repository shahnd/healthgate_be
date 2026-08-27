package com.kh.healthgate.safety.ai.rag;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import com.kh.healthgate.opendata.weather.model.vo.WeatherForecast;

public class WeatherContextTransformer {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final BigDecimal SNOWFALL_ZERO = new BigDecimal("0.0");

    /**
     * 기상청 습구온도 추정
     *
     * @param ta 기온 ℃
     * @param rh 상대습도 %
     * @return 습구온도
     */
    private static double estimateWetBulbTemperature(double ta, double rh) {
        return ta * Math.atan(0.151977 * Math.sqrt(rh + 8.313659))
                + Math.atan(ta + rh)
                - Math.atan(rh - 1.67633)
                + 0.00391838
                        * Math.pow(rh, 1.5)
                        * Math.atan(0.023101 * rh)
                - 4.686035;
    }

    /**
     * 기상청 여름철 체감온도 계산
     *
     * @param ta 기온 ℃
     * @param rh 상대습도 %
     * @return 여름철 체감온도
     */
    private static double calculateSummerApparentTemperature(double ta, double rh) {
        if (rh < 0 || rh > 100) {
            throw new IllegalArgumentException("상대습도는 0~100%여야 합니다.");
        }

        double tw = estimateWetBulbTemperature(ta, rh);

        return -0.2442
                + 0.55399 * tw
                + 0.45535 * ta
                - 0.0022 * tw * tw
                + 0.00278 * tw * ta
                + 3.0;
    }

    /**
     * 기상청 겨울철 체감온도 계산
     *
     * @param ta  기온
     * @param vms 풍속
     * @return 겨울철 체감온도
     */
    private static double calculateWinterWindChill(double ta, double vms) {
        if (vms < 0) {
            throw new IllegalArgumentException("풍속은 음수일 수 없습니다.");
        }

        double vkmh = vms * 3.6;
        double windFactor = Math.pow(vkmh, 0.16);

        return 13.12
                + 0.6215 * ta
                - 11.37 * windFactor
                + 0.3965 * windFactor * ta;
    }

    public static final Function<WeatherForecast, String> toWeatherContextLine = (WeatherForecast forecast) -> {
        int forecastMonth = forecast.getForecastAt().getMonthValue();
        boolean isSummer = List.of(5, 6, 7, 8, 9) // 기상청 여름철 기준
                .contains(forecastMonth);
        boolean isWinter = List.of(10, 11, 12, 1, 2, 3, 4) // 기상청 겨울철 기준
                .contains(forecastMonth);

        StringBuilder sb = new StringBuilder();

        sb.append(forecast.getForecastAt().format(TIME_FORMATTER));
        sb.append(" | ");
        sb.append("Temperature: %s°C".formatted(forecast.getTemperature()));
        sb.append(" | ");

        if (isSummer) {
            sb.append("Heat index: %s°C".formatted(calculateSummerApparentTemperature(
                    forecast.getTemperature().doubleValue(),
                    forecast.getHumidity().doubleValue())));
            sb.append(" | ");
        } else if (isWinter) {
            sb.append("Wind chill: %s°C".formatted(calculateWinterWindChill(
                    forecast.getTemperature().doubleValue(),
                    forecast.getWindSpeed().doubleValue())));
            sb.append(" | ");
        }

        sb.append("Humidity: %s%%".formatted(forecast.getHumidity()));
        sb.append(" | ");
        sb.append("Wind speed: %s m/s".formatted(forecast.getWindSpeed()));
        sb.append(" | ");
        sb.append("Sky condition: %s".formatted(forecast.getSkyCondition()));
        sb.append(" | ");

        sb.append("Precipitation probability: %s%%".formatted(forecast.getPrecipitationProbability()));
        sb.append(" | ");

        if (!forecast.getPrecipitation().equals("강수없음")) {
            sb.append("Precipitation type: %s".formatted(forecast.getPrecipitationType()));
            sb.append(" | ");
            sb.append("Precipitation: %s".formatted(forecast.getPrecipitation()));
            sb.append(" | ");
        }

        if (forecast.getSnowfall().equals(SNOWFALL_ZERO)) {
            sb.append("Snowfall: %s cm".formatted(forecast.getSnowfall()));
            sb.append(" | ");
        }

        return sb.delete(sb.length() - 3, sb.length()).toString().strip();
    };
}
