package com.example.nexus.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointConstants {
    public static final String AUTH = "/auth/**";
    public static final String ADMIN = "/admin/**";
    public static final String PRODUCT = "/product/**";
    public static final String CAMPAIGN = "/campaign/**";
}