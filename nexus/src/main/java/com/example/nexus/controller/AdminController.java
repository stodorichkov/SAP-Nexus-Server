package com.example.nexus.controller;

import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.response.AdminProductResponse;
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
import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
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

    @PatchMapping("/product/{productId}/campaign")
    ResponseEntity<?> removeProductCampaign(@PathVariable Long productId) {
        this.productService.removeProductCampaign(productId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/product")
    Page<AdminProductResponse> getProducts(Pageable pageable) {
        return this.productService.getProductsAdmin(pageable);
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    List<String> getCategories() {
        return this.categoryService.getCategories();
    }

    @PatchMapping("/campaign/{campaignName}/start")
    ResponseEntity<?> startCampaign(@PathVariable String campaignName) {
        this.campaignService.startCampaign(campaignName);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/campaign/{campaignName}/stop")
    ResponseEntity<?> stopCampaign(@PathVariable String campaignName) {
        this.campaignService.stopCampaign(campaignName);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/role/addition/{username}")
    public ResponseEntity<?> addUserRole(@PathVariable String username) {
        userService.addUserRole(username);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/role/removal/{username}")
    public ResponseEntity<?> removeUserRole(@PathVariable String username) {
        userService.removeUserRole(username);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/campaign/{campaignName}")
    Page<AdminProductResponse> getCampaignProducts(@PathVariable String campaignName, Pageable pageable) {
        return this.productService.getProductsByCampaignAdmin(campaignName, pageable);
    }
}