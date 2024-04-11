package com.example.nexus.model.payload.response;

import java.util.List;

public record UserResponse(String username, List<String> roles) {
}