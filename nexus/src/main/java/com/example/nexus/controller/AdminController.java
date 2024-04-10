package com.example.nexus.controller;

import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> getUsers(@RequestParam int pageNumber) {
        return this.userService
                .getUsers(pageNumber);
    }
}