package com.example.nexus.model.payload.response;

import java.time.LocalDate;

public record CampaignResponse(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isActive
) {
}