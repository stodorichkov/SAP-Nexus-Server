package com.example.nexus.model.payload.response;

import com.example.nexus.model.entity.Role;

import java.util.List;

public record UserResponse(String username,
                           String firstName,
                           String lastName,
                           List<Role> roles) {
}