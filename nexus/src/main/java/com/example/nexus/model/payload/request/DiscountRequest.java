package com.example.nexus.model.payload.request;

import com.example.nexus.constant.MessageConstants;
import jakarta.validation.constraints.*;

public record DiscountRequest(
        @NotNull(message = MessageConstants.FIELD_CANNOT_BE_BLANK)
        @Min(value = 0, message = MessageConstants.INVALID_DISCOUNT)
        @Max(value = 100, message = MessageConstants.INVALID_DISCOUNT)
        Integer discount
) {}