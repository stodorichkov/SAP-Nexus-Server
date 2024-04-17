package com.example.nexus.service;

import com.example.nexus.exception.CampaignAlreadyExistsException;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.CampaignMapper;
import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.CampaignRequest;
import com.example.nexus.model.payload.response.CampaignResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTests {
    private static Campaign campaign;
    private static Product product;
    private static CampaignRequest campaignRequest;
    private static CampaignResponse campaignResponse;

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
        campaign.setName("Campaign");
        campaign.setIsActive(false);

        product = new Product();
        product.setName("Product");
        product.setCampaign(campaign);
        product.setDiscount(10);
        product.setCampaignDiscount(20);

        campaignRequest = new CampaignRequest(
                "Campaign",
                LocalDate.parse("2024-01-01"),
                LocalDate.parse("2024-12-31")
        );

        campaignResponse = new CampaignResponse(
                "Campaign",
                LocalDate.parse("2024-01-01"),
                LocalDate.parse("2024-12-31"),
                false
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

        assertThrows(CampaignAlreadyExistsException.class,
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
        when(this.campaignMapper.campaignToCampaignResponse(any(Campaign.class))).thenReturn(campaignResponse);

        final var result = this.campaignService.getCampaigns(pageable);

        assertEquals(List.of(campaignResponse), result.getContent());
    }

    @Test
    void getCampaignsList_expectCampaignNamesList() {
        final var campaignsList = new ArrayList<>(List.of(campaign));

        when(this.campaignRepository.findAll()).thenReturn(campaignsList);

        final var result = this.campaignService.getCampaignsList();

        assertEquals(List.of("Campaign"), result);
    }
}