package com.example.nexus.model.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record TurnoverRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate
) {
}