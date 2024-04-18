package com.example.nexus.service;

import com.example.nexus.exception.FileUploadException;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProductMapper;
import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.entity.Category;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductCampaignRequest;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.response.AdminProductResponse;
import com.example.nexus.model.payload.response.ProductResponse;
import com.example.nexus.repository.CampaignRepository;
import com.example.nexus.repository.CategoryRepository;
import com.example.nexus.repository.ProductRepository;
import com.example.nexus.specification.ProductSpecifications;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {
    private static Product product;
    private static Campaign campaign;
    private static ProductRequest productRequest;
    private static ProductCampaignRequest productCampaignRequest;
    private static ProductResponse productResponse;
    private static AdminProductResponse adminProductResponse;
    private static Pageable pageable;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CampaignRepository campaignRepository;
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

    @BeforeEach
    void setUp() {
        final var category = new Category();
        category.setName("Category");

        campaign = new Campaign();
        campaign.setName("Campaign");

        product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setBrand("Brand");
        product.setDescription("Description");
        product.setCategory(category);
        product.setCampaign(campaign);
        product.setAvailability(20);
        product.setPrice(100f);
        product.setMinPrice(90f);
        product.setDiscount(10);
        product.setCampaignDiscount(20);
        product.setImageLink("url");

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
                10,
                file
        );

        productResponse = new ProductResponse(
                1L,
                "Product",
                "Brand",
                "Category",
                "Description",
                100f,
                0,
                "url"
        );

        adminProductResponse = new AdminProductResponse(
                1L,
                "Product",
                "Brand",
                "Category",
                "Campaign",
                "Description",
                10,
                100f,
                100f,
                0,
                0
        );

        productCampaignRequest = new ProductCampaignRequest(
                "Campaign",
                50
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

        this.productService.addProduct(productRequest);

        verify(this.productRepository).save(productCaptor.capture());

        assertEquals(product, productCaptor.getValue());
    }

    @Test
    void removeProductCampaign_productNotExist_expectNotFoundException() {
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->this.productService.removeProductCampaign(product.getId()));
    }

    @Test
    void removeProductCampaign_productExist_expectRemoveCampaign() {
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        this.productService.removeProductCampaign(product.getId());

        verify(this.productRepository).save(productCaptor.capture());

        assertAll(
                () -> assertNull(productCaptor.getValue().getCampaign()),
                () -> assertEquals(0, productCaptor.getValue().getDiscount()),
                () -> assertEquals(0, productCaptor.getValue().getCampaignDiscount()),
                () -> assertEquals(product.getId(), productCaptor.getValue().getId()),
                () -> assertEquals(product.getName(), productCaptor.getValue().getName()),
                () -> assertEquals(product.getBrand(), productCaptor.getValue().getBrand()),
                () -> assertEquals(product.getDescription(), productCaptor.getValue().getDescription()),
                () -> assertEquals(product.getCategory(), productCaptor.getValue().getCategory()),
                () -> assertEquals(product.getAvailability(), productCaptor.getValue().getAvailability()),
                () -> assertEquals(product.getPrice(), productCaptor.getValue().getPrice()),
                () -> assertEquals(product.getMinPrice(), productCaptor.getValue().getMinPrice()),
                () -> assertEquals(product.getImageLink(), productCaptor.getValue().getImageLink())
        );
    }

    @Test
    void addProductCampaign_productNotFound_expectNotFoundException() {
        when(this.productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> this.productService.addProductCampaign(1L, mock(ProductCampaignRequest.class)));
    }

    @Test
    void addProductCampaign_campaignNotFound_expectNotFoundException() {
        when(this.productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(this.campaignRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> this.productService.addProductCampaign(1L, productCampaignRequest));
    }

    @Test
    void addProductCampaign_campaignNotActive_expectAddProductWithoutDiscount() {
        product.setDiscount(0);

        when(this.productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(this.campaignRepository.findByName(anyString())).thenReturn(Optional.of(campaign));

        this.productService.addProductCampaign(product.getId(), productCampaignRequest);

        verify(this.productRepository).save(productCaptor.capture());
        assertAll(
                () -> assertEquals(campaign, productCaptor.getValue().getCampaign()),
                () -> assertEquals(0, productCaptor.getValue().getDiscount()),
                () -> assertEquals(product.getCampaignDiscount(), productCaptor.getValue().getCampaignDiscount()),
                () -> assertEquals(product.getId(), productCaptor.getValue().getId()),
                () -> assertEquals(product.getName(), productCaptor.getValue().getName()),
                () -> assertEquals(product.getBrand(), productCaptor.getValue().getBrand()),
                () -> assertEquals(product.getDescription(), productCaptor.getValue().getDescription()),
                () -> assertEquals(product.getCategory(), productCaptor.getValue().getCategory()),
                () -> assertEquals(product.getAvailability(), productCaptor.getValue().getAvailability()),
                () -> assertEquals(product.getPrice(), productCaptor.getValue().getPrice()),
                () -> assertEquals(product.getMinPrice(), productCaptor.getValue().getMinPrice()),
                () -> assertEquals(product.getImageLink(), productCaptor.getValue().getImageLink())
        );
    }

    @Test
    void addProductCampaign_campaignActive_expectAddProductWithDiscount() {
        product.setDiscount(0);
        campaign.setIsActive(true);

        when(this.productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(this.campaignRepository.findByName(anyString())).thenReturn(Optional.of(campaign));

        this.productService.addProductCampaign(product.getId(), productCampaignRequest);

        verify(this.productRepository).save(productCaptor.capture());
        assertAll(
                () -> assertEquals(campaign, productCaptor.getValue().getCampaign()),
                () -> assertNotEquals(0, productCaptor.getValue().getDiscount()),
                () -> assertEquals(product.getCampaignDiscount(), productCaptor.getValue().getCampaignDiscount()),
                () -> assertEquals(product.getId(), productCaptor.getValue().getId()),
                () -> assertEquals(product.getName(), productCaptor.getValue().getName()),
                () -> assertEquals(product.getBrand(), productCaptor.getValue().getBrand()),
                () -> assertEquals(product.getDescription(), productCaptor.getValue().getDescription()),
                () -> assertEquals(product.getCategory(), productCaptor.getValue().getCategory()),
                () -> assertEquals(product.getAvailability(), productCaptor.getValue().getAvailability()),
                () -> assertEquals(product.getPrice(), productCaptor.getValue().getPrice()),
                () -> assertEquals(product.getMinPrice(), productCaptor.getValue().getMinPrice()),
                () -> assertEquals(product.getImageLink(), productCaptor.getValue().getImageLink())
        );
    }

    @Test
    void getProducts_expectPage() {
        final var productPage = new PageImpl<>(List.of(product));
        final var specification = ProductSpecifications.findAvailable();

        when(this.productRepository.findAll(eq(specification), eq(pageable))).thenReturn(productPage);
        when(this.productMapper.productToProductResponse(eq(product))).thenReturn(productResponse);

        final var result = this.productService.getProducts(pageable);

        assertEquals(List.of(productResponse), result.getContent());
    }

    @Test
    void getPromoProducts_expectPage() {
        final var productPage = new PageImpl<>(List.of(product));

        when(this.productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(this.productMapper.productToProductResponse(eq(product))).thenReturn(productResponse);

        final var result = this.productService.getPromoProducts(pageable);

        assertEquals(List.of(productResponse), result.getContent());
    }

    @Test
    void getProductsByCampaign_expectPage() {
        final var productPage = new PageImpl<>(List.of(product));

        when(this.productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(this.productMapper.productToProductResponse(eq(product))).thenReturn(productResponse);

        final var result = this.productService.getProductsByCampaign("a", pageable);

        assertEquals(List.of(productResponse), result.getContent());
    }

    @Test
    void getProductsAdmin_expectPage() {
        final var productPage = new PageImpl<>(List.of(product));

        when(this.productRepository.findAll(eq(pageable))).thenReturn(productPage);
        when(this.productMapper.productToAdminProductResponse(eq(product))).thenReturn(adminProductResponse);

        final var result = this.productService.getProductsAdmin(pageable);

        assertEquals(List.of(adminProductResponse), result.getContent());
    }

    @Test
    void getProductsByCampaignAdmin_expectPage() {
        final var productPage = new PageImpl<>(List.of(product));

        when(this.productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(this.productMapper.productToAdminProductResponse(eq(product))).thenReturn(adminProductResponse);

        final var result = this.productService
                .getProductsByCampaignAdmin("a", pageable);

        assertEquals(List.of(adminProductResponse), result.getContent());
    }

    @Test
    void testEditProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
        when(fileService.upload(any())).thenReturn("imageUrl");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.editProduct(product.getId(), productRequest);

        assertEquals(productRequest.name(), product.getName());
        assertEquals(productRequest.brand(), product.getBrand());
        assertEquals(productRequest.description(), product.getDescription());
        assertEquals(productRequest.price(), product.getPrice());
        assertEquals(productRequest.minPrice(), product.getMinPrice());
        assertEquals(productRequest.discount(), product.getDiscount());
        assertEquals(productRequest.availability(), product.getAvailability());
    }

    @Test
    void testEditProduct_NotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            productService.editProduct(1L, productRequest);
        });
    }
}