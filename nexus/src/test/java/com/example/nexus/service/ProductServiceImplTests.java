package com.example.nexus.service;

import com.example.nexus.exception.FileUploadException;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProductMapper;
import com.example.nexus.model.entity.Category;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.repository.CategoryRepository;
import com.example.nexus.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {
    private static ProductRequest productRequest;
    private static Product product;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private FileService fileService;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeAll
    static void setUp() {
        final var category = new Category();
        category.setName("Category");

        final var content = new byte[]{};
        final var file = new MockMultipartFile("file",
                "test.png",
                "image/png",
                content
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

        product = new Product();
        product.setName("Product");
        product.setCategory(category);
        product.setBrand("Brand");
        product.setAvailability(20);
        product.setPrice(100f);
        product.setMinPrice(100f);
        product.setImageLink("url");
        product.setDescription("Description");
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

        assertThrows(FileUploadException.class, () -> productService.addProduct(productRequest));

        verifyNoInteractions(this.productMapper, this.productRepository);
    }

    @Test
    void addProduct_validData_expectSave() {
        when(this.categoryRepository.findByName(productRequest.category()))
                .thenReturn(Optional.ofNullable(product.getCategory()));
        when(this.fileService.upload(productRequest.image())).thenReturn("url");
        when(this.productMapper.productRequestToProduct(productRequest)).thenReturn(product);

        productService.addProduct(productRequest);

        final var productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(this.productRepository).save(productCaptor.capture());
        final var result = productCaptor.getValue();

        assertEquals(product, result);
    }
}