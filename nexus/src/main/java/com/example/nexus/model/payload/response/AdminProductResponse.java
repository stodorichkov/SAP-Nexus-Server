package com.example.nexus.model.payload.response;

public record AdminProductResponse(
    Long id,
    String name,
    String brand,
    String category,
    String campaign,
    String description,
    Integer availability,
    Float price,
    Float minPrice,
    Integer discount,
    Integer campaignDiscount,
    String imageLink
) {
}