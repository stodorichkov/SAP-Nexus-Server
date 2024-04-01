package com.example.nexus.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMappingConstants {
    public static final String AUTH = "/auth/**";
    public static final String ADMIN = "/admin/**";
}
