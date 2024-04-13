package com.example.nexus.model.payload.response;

public record ProfileInfoResponse(
        String username,
        String firstName,
        String lastName,
        Float balance
) {
}
