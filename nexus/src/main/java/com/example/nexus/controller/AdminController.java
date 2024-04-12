package com.example.nexus.controller;

import com.example.nexus.model.entity.Role;
import com.example.nexus.model.payload.request.RoleUpdateRequest;
import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.repository.UserRepository;
import com.example.nexus.service.JwtService;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.service.CampaignService;
import com.example.nexus.service.CategoryService;
import com.example.nexus.service.ProductService;
import com.example.nexus.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.nexus.model.payload.response.UserResponse;
import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CampaignService campaignService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> getUsers(Pageable pageable) {
        return this.userService.getUsers(pageable);
    }

    @PostMapping("/product")
    ResponseEntity<?> addProduct(@Valid @ModelAttribute ProductRequest productRequest) {
        this.productService.addProduct(productRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    List<String> getCategories() {
        return this.categoryService.getCategories();
    }

    @PatchMapping("/campaign/{campaignName}/stop")
    ResponseEntity<?> stopCampaign(@PathVariable String campaignName) {
        this.campaignService.stopCampaign(campaignName);

        return ResponseEntity.status(HttpStatus.OK).build();
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