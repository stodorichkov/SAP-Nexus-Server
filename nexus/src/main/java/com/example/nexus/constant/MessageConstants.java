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
    public static final String CAMPAIGN_NOT_FOUND = "Campaign not found!";
    public static final String PRODUCT_NOT_FOUND = "Product not found!";

    // exists
    public static final String USER_EXISTS = "User already exists!";

    // fields
    public static final String FIELD_CANNOT_BE_BLANK = "Field can not be blank!";

    // authentication
    public static final String INVALID_USERNAME_PASSWORD = "Invalid username or password!";
    public static final String INVALID_JWT = "JWT was expired or invalid!";

    // registration
    public static final String INVALID_USERNAME = "Invalid format for username. Must be at least 3 symbols long.";
    public static final String INVALID_FIRST_NAME = "Invalid format for first name. Name must start with " +
            "capital letter and contain only characters.";
    public static final String INVALID_LAST_NAME = "Invalid format for last name. Name must start with " +
            "capital letter and contain only characters.";
    public static final String INVALID_PASSWORD = "Invalid format for password. Password must be at least 8 " +
            "characters long, contain at least one digit, one uppercase letter and one lowercase letter!";
    public static final String PASSWORD_MISMATCH = "Repeated password doesn't match original!";

    // product
    public static final String INVALID_PRICE = "Invalid format for price. Price must be positive or zero!";
    public static final String INVALID_MIN_PRICE = "Invalid format for price. Price must be greater than or equal " +
            "to the min price!";
    public static final String INVALID_PRODUCT_NAME = "Invalid format for product name. Name must be between 2 and" +
            " 50 characters long and can contain only letters (both uppercase and lowercase), digits, and spaces!";
    public static final String INVALID_PRODUCT_BRAND = "Invalid format for product brand. Product brand name must " +
            "be between 2 and 30 characters long and can contain only letters (both uppercase and lowercase), " +
            "digits, and spaces.";
    public static final String INVALID_AVAILABILITY = "Invalid format for availability. Availability must be " +
            "positive or zero!";

    //campaign
    public static final String INVALID_START_DATE = "Invalid format for start date. Start date must be present or" +
            " future!";
    public static final String INVALID_END_DATE = "Invalid format for end date. End date must be greater than or" +
            " equal to the start date";

    // images
    public static final String FILE_UPLOAD_FAILURE = "File upload failed.";
}