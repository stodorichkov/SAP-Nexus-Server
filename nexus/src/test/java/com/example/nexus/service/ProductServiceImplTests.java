package com.example.nexus.service;

import com.example.nexus.exception.FileUploadException;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProductMapper;
import com.example.nexus.model.entity.Category;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.request.ProductsRequest;
import com.example.nexus.model.payload.response.ProductResponse;
import com.example.nexus.repository.CategoryRepository;
import com.example.nexus.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {
    private static Product product;
    private static ProductRequest productRequest;
    private static ProductsRequest productsRequest;
    private static ProductResponse productResponse;
    private static Pageable pageable;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private FileService fileService;
    @Captor
    private ArgumentCaptor<Product> productCaptor;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeAll
    static void setUp() {
        final var category = new Category();
        category.setName("Category");

        product = new Product();
        product.setName("Product");
        product.setBrand("Brand");
        product.setCategory(category);
        product.setAvailability(20);
        product.setPrice(100f);
        product.setMinPrice(100f);
        product.setImageLink("url");
        product.setDescription("Description");

        final var file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new byte[]{}
        );

        productRequest = new ProductRequest(
                "Product",
                "Brand",
                "Category",
                "Description",
                100f,
                100f,
                20,
                file
        );

        productsRequest = new ProductsRequest(
                null,
                null
        );

        productResponse = new ProductResponse(
                "Product",
                "Brand",
                "Category",
                "Description",
                100f,
                null,
                "url",
                null
        );

        pageable = Pageable.unpaged();
    }

    @Test
    void addProduct_categoryNotExist_expectNotFoundException() {
        when(this.categoryRepository.findByName(productRequest.category())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.productService.addProduct(productRequest));

        verify(this.categoryRepository).findByName(productRequest.category());
        verifyNoInteractions(this.fileService, this.productMapper, this.productRepository);
    }

    @Test
    void addProduct_invalidImage_expectFileUploadException() {
        when(this.categoryRepository.findByName(productRequest.category()))
                .thenReturn(Optional.ofNullable(product.getCategory()));
        when(this.fileService.upload(productRequest.image())).thenThrow(FileUploadException.class);

        verifyNoInteractions(this.productMapper, this.productRepository);

        assertThrows(FileUploadException.class, () -> productService.addProduct(productRequest));
    }

    @Test
    void addProduct_validData_expectSave() {
        when(this.categoryRepository.findByName(productRequest.category()))
                .thenReturn(Optional.ofNullable(product.getCategory()));
        when(this.fileService.upload(productRequest.image())).thenReturn("url");
        when(this.productMapper.productRequestToProduct(productRequest)).thenReturn(product);

        productService.addProduct(productRequest);

        verify(this.productRepository).save(productCaptor.capture());

        assertEquals(product, productCaptor.getValue());
    }

    @Test
    void getProducts_expectPage() {
        final var productPage = new PageImpl<>(List.of(product));

        when(this.productRepository.findAll(any(Specification.class), pageable)).thenReturn(productPage);
        when(this.productMapper.productToProductResponse(eq(product))).thenReturn(productResponse);

        final var result = this.productService.getProducts(productsRequest, pageable);

        assertEquals(List.of(productResponse), result.getContent());
    }
}