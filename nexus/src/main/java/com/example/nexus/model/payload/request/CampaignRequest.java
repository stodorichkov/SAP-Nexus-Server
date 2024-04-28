package com.example.nexus.model.payload.request;

import com.example.nexus.constant.MessageConstants;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record CampaignRequest(
        String name,
        @FutureOrPresent(message = MessageConstants.INVALID_START_DATE)
        LocalDate startDate,
        LocalDate endDate
) {
    @AssertTrue(message = MessageConstants.INVALID_END_DATE)
    public boolean isStartDateBeforeEndDate() {
        return startDate.isBefore(endDate) || startDate.isEqual(endDate);
    }
}