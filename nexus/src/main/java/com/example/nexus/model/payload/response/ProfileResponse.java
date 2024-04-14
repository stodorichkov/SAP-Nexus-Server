package com.example.nexus.model.payload.response;

public record ProfileResponse(
        String username,
        String firstName,
        String lastName,
        Float balance
) {
}
