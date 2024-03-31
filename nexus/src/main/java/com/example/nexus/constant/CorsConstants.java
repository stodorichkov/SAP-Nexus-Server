package com.example.nexus.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CorsConstants {
    public static final String MAPPING_PATTERN = "/api/**";
    public static final String ALLOWED_ORIGINS = "http://localhost:3000";
    public static final String ALLOWED_ALL = "*";
}