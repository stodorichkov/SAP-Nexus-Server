package com.example.nexus.service;

import com.example.nexus.exception.NotFoundException;
import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.entity.Product;
import com.example.nexus.repository.CampaignRepository;
import com.example.nexus.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTests {
    private static Campaign campaign;
    private static Product product;

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ProductRepository productRepository;
    @Captor
    private ArgumentCaptor<List<Product>> productsCaptor;
    @Captor
    private ArgumentCaptor<Campaign> campaignCaptor;
    @InjectMocks
    private CampaignServiceImpl campaignService;

    @BeforeAll
    static void setUp() {
        campaign = new Campaign();
        campaign.setName("Campaign");
        campaign.setIsActive(false);

        product = new Product();
        product.setName("Product");
        product.setCampaign(campaign);
        product.setDiscount(10);
        product.setCampaignDiscount(20);
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
}