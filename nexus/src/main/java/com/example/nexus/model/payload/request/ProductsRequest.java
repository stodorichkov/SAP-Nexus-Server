package com.example.nexus.model.payload.request;

import org.springframework.web.bind.annotation.RequestParam;

public record ProductsRequest(
        @RequestParam(required = false)
        Boolean promo,
        @RequestParam(required = false)
        String campaign
) {
}
