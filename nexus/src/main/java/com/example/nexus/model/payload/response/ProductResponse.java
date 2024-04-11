package com.example.nexus.model.payload.response;

public record ProductResponse(
        String name,
        String brand,
        String category,
        String description,
        Float price,
        Integer discount,
        String imageLink
) {
}
