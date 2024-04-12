package com.example.nexus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "image.product")
@Data
public class ImageConfig {
    private String dir;
    private String baseUrl;
}