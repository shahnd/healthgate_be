package com.kh.healthgate.opendata.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "com.kh.healthgate.opendata", ignoreUnknownFields = false)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OpenDataProperties {
    private String serviceKey;
    private String baseUrl;
}
