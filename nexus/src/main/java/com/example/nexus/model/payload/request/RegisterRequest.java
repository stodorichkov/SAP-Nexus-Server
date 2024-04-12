package com.example.nexus.model.payload.request;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.RegexConstants;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @Pattern(regexp = RegexConstants.NAME_REGEX, message = MessageConstants.INVALID_FIRST_NAME)
        String firstName,

        @Pattern(regexp = RegexConstants.NAME_REGEX, message = MessageConstants.INVALID_LAST_NAME)
        String lastName,

        @Pattern(regexp = RegexConstants.USERNAME_REGEX, message = MessageConstants.INVALID_USERNAME)
        String username,

        @Pattern(regexp = RegexConstants.PASSWORD_REGEX, message = MessageConstants.INVALID_PASSWORD)
        String password,

        @NotBlank(message = MessageConstants.FIELD_CANNOT_BE_BLANK)
        String confirmPassword
) {
    @AssertTrue(message = MessageConstants.PASSWORD_MISMATCH)
    public boolean isPasswordMatch() {
        return password().equals(confirmPassword());
    }
}