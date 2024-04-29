package com.example.nexus.service;

import com.example.nexus.exception.AlreadyExistsException;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.CampaignMapper;
import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.CampaignRequest;
import com.example.nexus.repository.CampaignRepository;
import com.example.nexus.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTests {
    private static Campaign campaign;
    private static Product product;
    private static CampaignRequest campaignRequest;

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private CampaignMapper campaignMapper;
    @Mock
    private ProductRepository productRepository;
    @Captor
    private ArgumentCaptor<List<Product>> productsCaptor;
    @Captor
    private ArgumentCaptor<Campaign> campaignCaptor;
    @InjectMocks
    private CampaignServiceImpl campaignService;

    @BeforeEach
    void setUp() {
        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("Campaign");
        campaign.setIsActive(false);

        product = new Product();
        product.setName("Product");
        product.setCampaign(campaign);
        product.setDiscount(10);
        product.setCampaignDiscount(20);

        campaignRequest = new CampaignRequest(
                "Campaign1",
                LocalDate.parse("2024-01-01"),
                LocalDate.parse("2024-12-31")
        );
    }

    @Test
    void starCampaign_campaignNotExist_expectNotFoundException() {
        assertThrows(NotFoundException.class, () -> this.campaignService.startCampaign(campaign.getName()));
    }

    @Test
    void startCampaign_campaignExist_expectStartCampaign() {
        when(this.campaignRepository.findByName(campaign.getName())).thenReturn(Optional.of(campaign));
        when(this.productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));

        this.campaignService.startCampaign(campaign.getName());

        verify(this.campaignRepository).save(campaignCaptor.capture());
        verify(this.productRepository).saveAll(productsCaptor.capture());

        assertAll(
                () -> assertEquals(campaign.getName(), campaignCaptor.getValue().getName()),
                () -> assertTrue(campaignCaptor.getValue().getIsActive()),
                () -> assertEquals(product.getCampaignDiscount(), productsCaptor.getValue().get(0).getDiscount())
        );
    }

    @Test
    void stopCampaign_campaignNotExist_expectNotFoundException() {
        assertThrows(NotFoundException.class, () -> this.campaignService.stopCampaign(campaign.getName()));
    }

    @Test
    void stopCampaign_campaignExist_expectStopCampaign() {
        when(this.campaignRepository.findByName(campaign.getName())).thenReturn(Optional.of(campaign));
        when(this.productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));

        this.campaignService.stopCampaign(campaign.getName());

        verify(this.campaignRepository).save(campaignCaptor.capture());
        verify(this.productRepository).saveAll(productsCaptor.capture());

        assertAll(
                () -> assertEquals(campaign.getName(), campaignCaptor.getValue().getName()),
                () -> assertNull(campaignCaptor.getValue().getStartDate()),
                () -> assertNull(campaignCaptor.getValue().getEndDate()),
                () -> assertFalse(campaignCaptor.getValue().getIsActive()),
                () -> assertNull(productsCaptor.getValue().get(0).getCategory()),
                () -> assertEquals(0, productsCaptor.getValue().get(0).getDiscount()),
                () -> assertEquals(0, productsCaptor.getValue().get(0).getCampaignDiscount())
        );
    }

    @Test
    void addCampaign_campaignExist_expectCampaignAlreadyExistsException() {
        when(this.campaignRepository.findByName(any(String.class))).thenReturn(Optional.of(campaign));

        assertThrows(AlreadyExistsException.class,
                () -> this.campaignService.addCampaign(campaignRequest));
    }

    @Test
    void addCampaign_EverythingIsFine_expectSaveNewCampaign() {
        campaign.setStartDate(LocalDate.parse("2024-01-01"));
        campaign.setEndDate(LocalDate.parse("2024-12-31"));

        when(this.campaignRepository.findByName(any(String.class))).thenReturn(Optional.empty());
        when(this.campaignMapper.campaignRequestToCampaign(any(CampaignRequest.class)))
                .thenReturn(campaign);

        this.campaignService.addCampaign(campaignRequest);

        verify(this.campaignRepository).save(campaignCaptor.capture());

        assertAll(
                () -> assertEquals(campaign.getName(), campaignCaptor.getValue().getName()),
                () -> assertEquals(campaign.getStartDate(), campaignCaptor.getValue().getStartDate()),
                () -> assertEquals(campaign.getEndDate(), campaignCaptor.getValue().getEndDate()),
                () -> assertEquals(campaign.getIsActive(), campaignCaptor.getValue().getIsActive())
        );
    }

    @Test
    void getCampaigns_expectPage() {
        campaign.setStartDate(LocalDate.parse("2024-01-01"));
        campaign.setEndDate(LocalDate.parse("2024-12-31"));

        final var campaignPage = new PageImpl<>(List.of(campaign));
        final var pageable = Pageable.unpaged();

        when(this.campaignRepository.findAll(eq(pageable))).thenReturn(campaignPage);

        final var result = this.campaignService.getCampaigns(pageable);

        assertEquals(campaignPage.getContent(), result.getContent());
    }

    @Test
    void getCampaignsList_expectCampaignNamesList() {
        final var campaignsList = new ArrayList<>(List.of(campaign));

        when(this.campaignRepository.findAll()).thenReturn(campaignsList);

        final var result = this.campaignService.getCampaignsList();

        assertEquals(List.of("Campaign"), result);
    }

    @Test
    void getActiveCampaigns_activeCampaignsExist_expectActiveCampaigns() {
        campaign.setIsActive(true);
        final var campaigns = Collections.singletonList(campaign);

        when(this.campaignRepository.findAll(any(Specification.class))).thenReturn(campaigns);

        final var result = campaignService.getActiveCampaigns();

        assertAll(
                () -> assertEquals(1, result.size()),
                () ->assertEquals(campaign.getName(), result.get(0))
        );
    }

    @Test
    public void editCampaign_campaignNotExist_expectNotFoundException() {
        assertThrows(
                NotFoundException.class,
                () -> this.campaignService.editCampaign(campaign.getId(), campaignRequest)
        );
    }

    @Test
    public void editCampaign() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        campaignService.editCampaign(1L, campaignRequest);

        verify(campaignMapper).updateCampaign(campaignRequest, campaign);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void RemoveCampaign() {
        final var campaignName = "Campaign";
        final var products = List.of(product);

        when(campaignRepository.findByName(campaignName)).thenReturn(Optional.of(campaign));
        when(productRepository.findAllByCampaign(campaign)).thenReturn(products);

        campaignService.removeCampaign(campaignName);

        verify(campaignRepository, times(1)).findByName(campaignName);
        verify(productRepository, times(1)).findAllByCampaign(campaign);
        verify(productRepository, times(1)).saveAll(productsCaptor.capture());
        verify(campaignRepository, times(1)).delete(campaignCaptor.capture());

        final var capturedProducts = productsCaptor.getValue();
        final var capturedCampaign = campaignCaptor.getValue();

        assertEquals(0, capturedProducts.get(0).getCampaignDiscount());
        assertNull(capturedProducts.get(0).getCampaign());
        assertEquals(campaign, capturedCampaign);
    }
}