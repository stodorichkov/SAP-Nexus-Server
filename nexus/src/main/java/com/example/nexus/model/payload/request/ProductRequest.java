package com.example.nexus.model.payload.request;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.RegexConstants;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public record ProductRequest(
        @Pattern(regexp = RegexConstants.PRODUCT_NAME_REGEX, message = MessageConstants.INVALID_PRODUCT_NAME)
        String name,

        @Pattern(regexp = RegexConstants.PRODUCT_BRAND_REGEX, message = MessageConstants.INVALID_PRODUCT_BRAND)
        String brand,

        @NotBlank(message = MessageConstants.FIELD_CANNOT_BE_BLANK)
        String category,

        String description,

        @PositiveOrZero(message = MessageConstants.INVALID_PRICE)
        Float price,

        @PositiveOrZero(message = MessageConstants.INVALID_PRICE)
        Float minPrice,

        @PositiveOrZero(message = MessageConstants.INVALID_AVAILABILITY)
        int availability,

        @RequestParam
        @Min(value = 0, message = MessageConstants.INVALID_DISCOUNT)
        @Max(value = 100, message = MessageConstants.INVALID_DISCOUNT)
        Integer discount,

        MultipartFile image
) {
    @AssertTrue(message = MessageConstants.INVALID_MIN_PRICE)
    public boolean isPriceGreaterOrEqualToMinPrice() {
        return price >= minPrice;
    }
}