package com.example.nexus.config;

import com.example.nexus.constant.CorsConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping(CorsConstants.MAPPING_PATTERN)
                .allowedOrigins(CorsConstants.ALLOWED_ORIGINS)
                .allowedMethods(CorsConstants.ALLOWED_ALL)
                .allowedHeaders(CorsConstants.ALLOWED_ALL)
                .allowCredentials(true);
    }
}