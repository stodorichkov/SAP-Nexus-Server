package com.example.nexus.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CorsConstants {
    public static final String MAPPING_PATTERN = "/**";
    public static final String ALLOWED_ORIGINS = "http://localhost:5173";
    public static final String ALLOWED_ALL = "*";
}