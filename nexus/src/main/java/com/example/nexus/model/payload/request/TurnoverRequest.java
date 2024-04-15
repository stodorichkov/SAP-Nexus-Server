package com.example.nexus.model.payload.request;

import com.example.nexus.constant.MessageConstants;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

public record TurnoverRequest(
        @RequestParam
        LocalDate startDate,
        @RequestParam
        LocalDate endDate
) {
        @AssertTrue(message = MessageConstants.INVALID_SALE_DATE)
        public boolean isStartDateBeforeEndDate() { return startDate.isBefore(endDate); }
}