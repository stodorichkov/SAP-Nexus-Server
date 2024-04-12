package com.example.nexus.controller;

import com.example.nexus.model.payload.request.AddMoneyRequest;
import com.example.nexus.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PatchMapping("/money")
    public ResponseEntity<?> addMoney(@RequestBody AddMoneyRequest addMoneyRequest, HttpServletRequest request) {
        this.profileService.addMoney(addMoneyRequest, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
