package com.example.nexus.service;

import com.example.nexus.exception.NotFoundException;
import com.example.nexus.exception.PaymentException;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.Sale;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.TurnoverRequest;
import com.example.nexus.repository.ProductRepository;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.SaleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceImplTests {
    private static Product product;
    private static Profile profile;
    private static String token;
    private static TurnoverRequest turnoverRequest;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private SaleRepository saleRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest servletRequest;
    @Captor
    private ArgumentCaptor<Sale> saleCaptor;
    @Captor
    private ArgumentCaptor<Product> productCaptor;
    @Captor
    private  ArgumentCaptor<Profile> profileCaptor;
    @InjectMocks
    private SaleServiceImpl saleService;

    @BeforeAll
    static void beforeAll() {
        token = "token";
    }

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setAvailability(0);
        product.setPrice(200f);
        product.setDiscount(10);

        profile = new Profile();
        profile.setUser(new User());
        profile.getUser().setUsername("username");
        profile.setBalance(0f);

        turnoverRequest = new TurnoverRequest(
                LocalDate.parse("2024-01-01"),
                LocalDate.parse("2024-12-31")
        );

        lenient().when(jwtService.getTokenFromRequest(servletRequest)).thenReturn(token);
        lenient().when(jwtService.getUsernameFromToken(token)).thenReturn(profile.getUser().getUsername());
    }

    @Test
    void buyProduct_profileNotExist_expectNotFoundException() {
        when(this.profileRepository.findByUserUsername(profile.getUser().getUsername())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.saleService.buyProduct(product.getId(), servletRequest));
    }

    @Test
    void buyProduct_productNotExist_expectNotFoundException() {
        when(this.profileRepository.findByUserUsername(profile.getUser().getUsername())).thenReturn(Optional.of(profile));
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.saleService.buyProduct(product.getId(), servletRequest));
    }

    @Test
    void buyProduct_productUnavailable_expectNotFoundException() {
        when(this.profileRepository.findByUserUsername(profile.getUser().getUsername())).thenReturn(Optional.of(profile));
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class, () -> this.saleService.buyProduct(product.getId(), servletRequest));
    }

    @Test
    void buyProduct_notEnoughBalance_expectPaymentException() {
        product.setAvailability(10);

        when(this.profileRepository.findByUserUsername(profile.getUser().getUsername())).thenReturn(Optional.of(profile));
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(PaymentException.class, () -> this.saleService.buyProduct(product.getId(), servletRequest));
    }

    @Test
    void buyProduct_enoughBalance_expectBuyProduct() {
        product.setAvailability(10);
        profile.setBalance(300f);

        final var actualPrice = product.getPrice() - product.getDiscount() * 0.01F * product.getPrice();
        final var remainBalance = profile.getBalance() - actualPrice;

        when(this.profileRepository.findByUserUsername(profile.getUser().getUsername())).thenReturn(Optional.of(profile));
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        this.saleService.buyProduct(product.getId(), servletRequest);

        verify(this.saleRepository).save(saleCaptor.capture());
        verify(this.productRepository).save(productCaptor.capture());
        verify(this.profileRepository).save(profileCaptor.capture());

        assertAll(
                () -> assertEquals(product, saleCaptor.getValue().getProduct()),
                () -> assertEquals(profile, saleCaptor.getValue().getProfile()),
                () -> assertEquals(actualPrice, saleCaptor.getValue().getPrice()),
                () -> assertEquals(LocalDate.now(), saleCaptor.getValue().getSaleDate()),
                () -> assertEquals(9, productCaptor.getValue().getAvailability()),
                () -> assertEquals(remainBalance, profileCaptor.getValue().getBalance())
        );
    }

    @Test
    void getTurnover_noSalesBetweenTheSpecifiedDates_expectNotFoundException() {
        when(this.saleRepository.findSumByStartAndEndDate(
                any(LocalDate.class), any(LocalDate.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.saleService.getTurnover(turnoverRequest));
    }

    @Test
    void getTurnover_everythingIsCorrect_returnFloat() {
        when(this.saleRepository.findSumByStartAndEndDate(
                any(LocalDate.class), any(LocalDate.class))).thenReturn(Optional.of(69.99f));

        assertEquals(69.99f, this.saleService.getTurnover(turnoverRequest));
    }
}