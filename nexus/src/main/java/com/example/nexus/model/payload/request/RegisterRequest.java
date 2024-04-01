package com.example.nexus.model.payload.request;

public record RegisterRequest(String firstName, String lastName, Float balance,
         String username, String password, String confirmPassword) {
}
