package com.example.nexus.controller;

import com.example.nexus.model.entity.Role;
import com.example.nexus.model.payload.request.RoleUpdateRequest;
import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.repository.UserRepository;
import com.example.nexus.service.JwtService;
import com.example.nexus.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> getUsers(@RequestParam int pageNumber) {
        return this.userService
                .getUsers(pageNumber);
    }

    @PatchMapping("/role/addition")
    public ResponseEntity<?> addUserRole(@RequestBody RoleUpdateRequest request) {
        userService.addUserRole(request);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/role/removal")
    public ResponseEntity<?> removeUserRole(@RequestBody RoleUpdateRequest request) {
        userService.removeUserRole(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}