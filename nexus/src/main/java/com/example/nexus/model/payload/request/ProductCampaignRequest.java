package com.example.nexus.model.payload.request;

import com.example.nexus.constant.MessageConstants;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductCampaignRequest(
        String campaignName,
        @PositiveOrZero
        int campaignDiscount
) {
    @AssertTrue(message = MessageConstants.INVALID_DISCOUNT)
    public boolean isDiscountCorrect() { return campaignDiscount <= 100; }
}