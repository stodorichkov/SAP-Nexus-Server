package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProductMapper;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.request.ProductsRequest;
import com.example.nexus.model.payload.response.ProductResponse;
import com.example.nexus.repository.CategoryRepository;
import com.example.nexus.repository.ProductRepository;
import com.example.nexus.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Override
    public void addProduct(ProductRequest productRequest) {
        final var category = this.categoryRepository
                .findByName(productRequest.category())
                .orElseThrow(() -> new NotFoundException(MessageConstants.CATEGORY_NOT_FOUND));
        final var imageUrl = this.fileService.upload(productRequest.image());

        final var product = this.productMapper.productRequestToProduct(productRequest);
        product.setCategory(category);
        product.setImageLink(imageUrl);

        this.productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> getProducts(ProductsRequest productRequest, Pageable pageable) {
        final var specification = ProductSpecifications.getProductSpecifications(productRequest);

        return this.productRepository.findAll(specification, pageable).map(null);
    }
}