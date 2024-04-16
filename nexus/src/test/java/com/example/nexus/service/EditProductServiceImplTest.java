package com.example.nexus.service;

import com.example.nexus.model.entity.Category;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.repository.CategoryRepository;
import com.example.nexus.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditProductServiceImplTest {
    private static Product product;
    private static ProductRequest productRequest;

    @InjectMocks
    private static EditProductServiceImpl editProductService;

    @Mock
    private static ProductRepository productRepository;

    @Mock
    private static CategoryRepository categoryRepository;

    @Mock
    private static FileService fileService;

    @BeforeAll
    static void setUp() {
        product = new Product();
        product.setId(1L);

        MockMultipartFile mockImageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
        productRequest = new ProductRequest("name", "brand", "category", "description", 100.0f, 90.0f, 10, 10, mockImageFile);
    }

    @Test
    void testEditProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
        when(fileService.upload(any())).thenReturn("imageUrl");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        editProductService.editProduct(product.getId(), productRequest);

        assertEquals(productRequest.name(), product.getName());
        assertEquals(productRequest.brand(), product.getBrand());
        assertEquals(productRequest.description(), product.getDescription());
        assertEquals(productRequest.price(), product.getPrice());
        assertEquals(productRequest.minPrice(), product.getMinPrice());
        assertEquals(productRequest.discount(), product.getDiscount());
        assertEquals(productRequest.availability(), product.getAvailability());
    }
}