package com.example.nexus.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexConstants {
    //at least 8 characters, one digit, one lowercase and one uppercase character.
    public static String PASSWORD_REQUIREMENT_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
}