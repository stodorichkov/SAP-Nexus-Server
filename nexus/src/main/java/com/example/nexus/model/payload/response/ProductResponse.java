package com.example.nexus.model.payload.response;

public record ProductResponse(
        Long id,
        String name,
        String brand,
        String category,
        String description,
        Float price,
        Integer discount,
        String imageLink
) {
}
