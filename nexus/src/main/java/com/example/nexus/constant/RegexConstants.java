package com.example.nexus.constant;

public class RegexConstants {
    //at least 8 characters, one digit, one lowercase and one uppercase character.
    public static String PASSWORD_REQUIREMENT_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
}
