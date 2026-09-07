package com.kh.healthgate.safety.seed;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(
        prefix = "com.kh.healthgate.safety.seed",
        ignoreUnknownFields = false)
public class SafetyDocumentSeedProperties {
    private boolean enabled;
    private String directory = "./data/documents";
}
