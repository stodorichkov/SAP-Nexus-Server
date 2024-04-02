package com.example.nexus.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageConstants {
    public static final String INTERNAL_SERVER_ERROR = "Internal server error!";
    public static final String USER_NOT_FOUND = "User not found!";
    public static final String INVALID_USERNAME_PASSWORD = "Invalid username or password!";
    public static final String ROLE_NOT_FOUNT = "Role not found!";
    public static final String CONFIRM_PASSWORD_NOT_MATCHING = "Repeated password doesn't match original!";
    public static final String WRONG_PASSWORD_FORMAT = "Password must be at least 8 characters long," +
            " contain at least one digit, one uppercase letter and one lowercase letter!";
    public static final String USER_ALREADY_EXISTS = "User already exists!";
}
