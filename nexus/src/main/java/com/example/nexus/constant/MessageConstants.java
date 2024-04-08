package com.example.nexus.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageConstants {
    // server errors
    public static final String INTERNAL_SERVER_ERROR = "Internal server error!";

    // not found
    public static final String USER_NOT_FOUND = "User not found!";
    public static final String CATEGORY_NOT_FOUND = "Category not found!";
    public static final String ROLE_NOT_FOUNT = "Role not found!";

    // exists
    public static final String USER_EXISTS = "User already exists!";

    // fields
    public static final String FIELD_CANNOT_BE_BLANK = "Field can not be blank!";

    // authentication
    public static final String INVALID_USERNAME_PASSWORD = "Invalid username or password!";
    public static final String INVALID_JWT = "JWT was expired or invalid!";

    // registration
    public static final String INVALID_USERNAME = "Invalid format for username. Must be at least 3 symbols long.";
    public static final String INVALID_FORMAT_FOR_FIRST_NAME = "Invalid format for first name. Name must start with " +
            "capital letter and contain only characters.";
    public static final String INVALID_FORMAT_FOR_LAST_NAME = "Invalid format for last name. Name must start with " +
            "capital letter and contain only characters.";
    public static final String INVALID_PASSWORD = "Password must be at least 8 characters long," +
            " contain at least one digit, one uppercase letter and one lowercase letter!";
    public static final String PASSWORD_MISMATCH = "Repeated password doesn't match original!";

    // product
    public static final String INVALID_PRICE = "Invalid price!";
}