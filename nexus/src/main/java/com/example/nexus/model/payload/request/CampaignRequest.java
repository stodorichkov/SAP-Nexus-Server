package com.example.nexus.model.payload.request;

import com.example.nexus.constant.MessageConstants;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;

public record CampaignRequest(
        String name,
        LocalDate startDate,
        LocalDate endDate
) {
    @AssertTrue(message = MessageConstants.INVALID_END_DATE)
    public boolean isStartDateBeforeEndDate() { return startDate.isBefore(endDate); }
}