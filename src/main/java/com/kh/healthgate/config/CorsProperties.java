package com.kh.healthgate.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "com.kh.healthgate.cors", ignoreUnknownFields = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class CorsProperties {
    private List<String> allowedOrigins;
}
