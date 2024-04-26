package com.example.nexus.controller;

import com.example.nexus.model.payload.request.CampaignRequest;
import com.example.nexus.model.payload.request.DiscountRequest;
import com.example.nexus.model.payload.request.ProductCampaignRequest;
import com.example.nexus.model.payload.request.TurnoverRequest;
import com.example.nexus.model.payload.response.CampaignResponse;
import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.response.AdminProductResponse;
import com.example.nexus.service.*;
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
    private final SaleService saleService;

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

    @PatchMapping("/product/{productId}/campaign/removal")
    ResponseEntity<?> removeProductCampaign(@PathVariable Long productId) {
        this.productService.removeProductCampaign(productId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/product/{productId}/campaign/addition")
    ResponseEntity<?> addProductCampaign(@PathVariable Long productId,
                                         @RequestBody ProductCampaignRequest productCampaignRequest) {
        this.productService.addProductCampaign(productId, productCampaignRequest);

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

    @GetMapping("/turnover")
    public ResponseEntity<Float> getTurnover(TurnoverRequest turnoverRequest) {
        Float turnover = this.saleService.getTurnover(turnoverRequest);

        return ResponseEntity.ok(turnover);
    }

    @PostMapping("/campaign")
    public ResponseEntity<?> addCampaign(@RequestBody CampaignRequest campaignRequest) {
        this.campaignService.addCampaign(campaignRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/product/{productId}")
    public ResponseEntity<?> editProduct(@PathVariable Long productId, @Valid @ModelAttribute ProductRequest productRequest) {
        this.productService.editProduct(productId, productRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/product/{productId}/campaignDiscount")
    public ResponseEntity<?> editProductCampaignDiscount(@PathVariable Long productId, @Valid @RequestBody DiscountRequest discountRequest) {
        this.productService.editProductCampaignDiscount(productId, discountRequest.discount());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/campaigns")
    @ResponseStatus(HttpStatus.OK)
    public Page<CampaignResponse> getCampaigns(Pageable pageable) {
        return this.campaignService.getCampaigns(pageable);
    }

    @GetMapping("/campaigns/list")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getCampaignsList() {
        return this.campaignService.getCampaignsList();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> removeProduct(@PathVariable Long productId) {
        productService.removeProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/campaigns/active")
    @ResponseStatus(HttpStatus.OK)
    public List<CampaignResponse> getActiveCampaigns() {
        return this.campaignService.getActiveCampaigns();
    }

    @PatchMapping("/campaign/{campaignName}")
    public ResponseEntity<?> editCampaign(@PathVariable String campaignName, @RequestBody CampaignRequest campaignRequest) {
        this.campaignService.editCampaign(campaignName, campaignRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}