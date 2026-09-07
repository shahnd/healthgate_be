package com.kh.healthgate.file.config;

import java.nio.file.Path;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kh.healthgate.file.storage.FileStorage;
import com.kh.healthgate.file.storage.LocalFileStorage;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStorageConfig {
    @Bean
    FileStorage fileStorage(FileStorageProperties properties) {
        return new LocalFileStorage(Path.of(properties.getRootDir()));
    }
}
