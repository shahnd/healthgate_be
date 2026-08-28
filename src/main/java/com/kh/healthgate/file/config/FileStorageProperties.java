package com.kh.healthgate.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "com.kh.healthgate.file", ignoreUnknownFields = false)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileStorageProperties {
    private String rootDir;
}
