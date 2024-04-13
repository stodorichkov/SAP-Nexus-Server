package com.example.nexus.controller;

import com.example.nexus.model.payload.response.ProfileInfoResponse;
import com.example.nexus.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<ProfileInfoResponse> getUserInfo(HttpServletRequest request) {
        final var profileInfoResponse = this.userService.getProfileInfo(request);

        return ResponseEntity.ok(profileInfoResponse);
    }
}
