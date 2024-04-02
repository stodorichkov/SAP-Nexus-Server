package com.example.nexus.model.payload.request;

public record RegisterRequest(String firstName, String lastName,
         String username, String password, String confirmPassword) {
}