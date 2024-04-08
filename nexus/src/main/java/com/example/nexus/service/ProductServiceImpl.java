package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.BadRequestException;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProductMapper;
import com.example.nexus.model.entity.Category;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public void addProduct(ProductRequest productRequest) {
        this.validatePrice(productRequest.price(), productRequest.minPrice());
        final var category = this.validateCategory(productRequest.category());

    }

    private void validatePrice(Float price, Float minPrice) {
        if(price < minPrice || price < 0 || minPrice < 0) {
            throw new BadRequestException(MessageConstants.INVALID_PRICE);
        }
    }

    private Category validateCategory(String category) {
        return this.categoryRepository
                .findByName(category)
                .orElseThrow(() -> new NotFoundException(MessageConstants.CATEGORY_NOT_FOUND));
    }
}
