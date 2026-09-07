package com.kh.healthgate.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.kh.healthgate.file")
public class FileStorageProperties {
    private String rootDir = "./data/storage";
}
