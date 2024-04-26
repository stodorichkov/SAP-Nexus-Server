package com.example.nexus.service;

import com.example.nexus.exception.BadRequestException;
import com.example.nexus.exception.FileUploadException;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProductMapper;
import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.entity.Category;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductCampaignRequest;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.request.ProductsRequest;
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
    private static ProductRequest productRequestValidDiscount;
    private static ProductRequest productRequestInvalidDiscount;
    private static ProductsRequest productsRequest;
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
        product.setMinPrice(70f);
        product.setDiscount(20);
        product.setCampaignDiscount(30);
        product.setImageLink("url");

        final var file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new byte[]{}
        );

        productRequestValidDiscount = new ProductRequest(
                "Product",
                "Brand",
                "Category",
                "Description",
                100f,
                90f,
                20,
                10,
                file
        );

        productRequestInvalidDiscount = new ProductRequest(
                "Product",
                "Brand",
                "Category",
                "Description",
                100f,
                90f,
                20,
                20,
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

        productsRequest = new ProductsRequest(
                true,
                campaign.getName()
        );

        pageable = Pageable.unpaged();
    }

    @Test
    void addProduct_categoryNotExist_expectNotFoundException() {
        when(this.categoryRepository.findByName(productRequestValidDiscount.category())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.productService.addProduct(productRequestValidDiscount));

        verify(this.categoryRepository).findByName(productRequestValidDiscount.category());
        verifyNoInteractions(this.fileService, this.productMapper, this.productRepository);
    }

    @Test
    void addProduct_invalidImage_expectFileUploadException() {
        when(this.categoryRepository.findByName(productRequestValidDiscount.category()))
                .thenReturn(Optional.ofNullable(product.getCategory()));
        when(this.fileService.upload(productRequestValidDiscount.image())).thenThrow(FileUploadException.class);

        verifyNoInteractions(this.productMapper, this.productRepository);

        assertThrows(FileUploadException.class, () -> productService.addProduct(productRequestValidDiscount));
    }

    @Test
    void addProduct_validData_expectSave() {
        when(this.categoryRepository.findByName(productRequestValidDiscount.category()))
                .thenReturn(Optional.ofNullable(product.getCategory()));
        when(this.fileService.upload(productRequestValidDiscount.image())).thenReturn("url");
        when(this.productMapper.productRequestToProduct(productRequestValidDiscount)).thenReturn(product);

        this.productService.addProduct(productRequestValidDiscount);

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

        when(this.productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(this.productMapper.productToProductResponse(eq(product))).thenReturn(productResponse);

        final var result = this.productService.getProducts(productsRequest, pageable);

        assertEquals(List.of(productResponse), result.getContent());
    }

    @Test
    void getProductsAdmin_expectPage() {
        final var productPage = new PageImpl<>(List.of(product));

        when(this.productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(this.productMapper.productToAdminProductResponse(eq(product))).thenReturn(adminProductResponse);

        final var result = this.productService.getProductsAdmin(productsRequest, pageable);

        assertEquals(List.of(adminProductResponse), result.getContent());
    }

    @Test
    void editProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
        when(fileService.upload(any())).thenReturn("imageUrl");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.editProduct(product.getId(), productRequestValidDiscount);

        assertAll(
            () -> assertEquals(productRequestValidDiscount.name(), product.getName()),
            () -> assertEquals(productRequestValidDiscount.brand(), product.getBrand()),
            () -> assertEquals(productRequestValidDiscount.description(), product.getDescription()),
            () -> assertEquals(productRequestValidDiscount.price(), product.getPrice()),
            () -> assertEquals(productRequestValidDiscount.minPrice(), product.getMinPrice()),
            () -> assertEquals(productRequestValidDiscount.discount(), product.getDiscount()),
            () -> assertEquals(productRequestValidDiscount.availability(), product.getAvailability())
        );
    }

    @Test
    void editProduct_BadRequestException() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));

        assertThrows(BadRequestException.class, () ->
                productService.editProduct(1L, productRequestInvalidDiscount)
        );
    }

    @Test
    void editProduct_NotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                productService.editProduct(1L, productRequestValidDiscount)
        );
    }

    @Test
    public void removeProduct() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        productService.removeProduct(1L);

        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).delete(argumentCaptor.capture());
        assertEquals(product.getId(), argumentCaptor.getValue().getId());
    }


    @Test
    public void removeProduct_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.removeProduct(1L));
    }

    @Test
    void editProductCampaignDiscount_NotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                productService.editProductCampaignDiscount(1L, 50)
        );
    }

    @Test
    void editProductCampaignDiscount_BadRequestException() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () ->
                productService.editProductCampaignDiscount(1L, 50)
        );
    }

    @Test
    void editProductCampaignDiscount_onlyCampaignDiscount() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        campaign.setIsActive(false);

        productService.editProductCampaignDiscount(1L, 10);

        verify(this.productRepository).save(productCaptor.capture());

        assertAll(
                () -> assertEquals(10, productCaptor.getValue().getCampaignDiscount()),
                () -> assertEquals(product.getDiscount(), productCaptor.getValue().getDiscount())
        );
    }

    @Test
    void editProductCampaignDiscount_bothDiscounts() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        campaign.setIsActive(true);

        productService.editProductCampaignDiscount(1L, 10);

        verify(this.productRepository).save(productCaptor.capture());

        assertAll(
                () -> assertEquals(10, productCaptor.getValue().getCampaignDiscount()),
                () -> assertEquals(10, productCaptor.getValue().getDiscount())
        );
    }
}