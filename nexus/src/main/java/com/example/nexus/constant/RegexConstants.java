package com.example.nexus.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexConstants {
    //at least 8 characters, one digit, one lowercase and one uppercase character.
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_.-]{3,50}$";
    public static final String NAME_REGEX = "^[A-Z][a-z]{2,30}$";
    public static final String PRODUCT_NAME_REGEX = "^[a-zA-Z0-9\\s]{2,50}$";
    public static final String PRODUCT_BRAND_REGEX = "^[a-zA-Z0-9\\s]{2,30}$";
}