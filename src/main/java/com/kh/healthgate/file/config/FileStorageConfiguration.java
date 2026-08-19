package com.kh.healthgate.file.config;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kh.healthgate.file.FileStorage;
import com.kh.healthgate.file.LocalFileStorage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "com.kh.healthgate.file", ignoreUnknownFields = false)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileStorageConfiguration {
    private String rootDir;

    @Bean
    FileStorage fileStorage() {
        return new LocalFileStorage(Path.of(rootDir));
    }
}
