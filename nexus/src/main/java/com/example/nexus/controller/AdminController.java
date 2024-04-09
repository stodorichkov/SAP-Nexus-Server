package com.example.nexus.controller;

import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.service.CategoryService;
import com.example.nexus.service.ProductService;
import com.example.nexus.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> getUsers(@RequestParam int pageNumber) {
        return this.userService.getUsers(pageNumber);
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
}