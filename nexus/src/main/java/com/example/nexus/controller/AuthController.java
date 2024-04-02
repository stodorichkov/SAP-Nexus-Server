package com.example.nexus.controller;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.request.AuthenticationRequest;
import com.example.nexus.model.payload.request.RegisterRequest;
import com.example.nexus.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        final var token = this.authService.login(authenticationRequest);
        final var headers = new HttpHeaders();

        headers.setBearerAuth(token);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }

    @PostMapping("/registration")
    ResponseEntity<Profile> register(@RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }
}