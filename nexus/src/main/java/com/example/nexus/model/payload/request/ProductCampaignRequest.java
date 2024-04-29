package com.example.nexus.model.payload.request;

import com.example.nexus.constant.MessageConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ProductCampaignRequest(
        String campaignName,

        @Min(value = 0, message = MessageConstants.INVALID_DISCOUNT)
        @Max(value = 100, message = MessageConstants.INVALID_DISCOUNT)
        int campaignDiscount
) {
}