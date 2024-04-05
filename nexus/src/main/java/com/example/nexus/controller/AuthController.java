package com.example.nexus.controller;

import com.example.nexus.model.payload.request.AuthenticationRequest;
import com.example.nexus.model.payload.request.RegisterRequest;
import com.example.nexus.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
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
    ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}