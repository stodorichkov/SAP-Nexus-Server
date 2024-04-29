package com.example.nexus.model.payload.request;

import jakarta.validation.constraints.*;
import com.example.nexus.constant.MessageConstants;

public record AddMoneyRequest(
        @PositiveOrZero(message = MessageConstants.INVALID_MONEY_NUMBER)
        float money
) {
}
