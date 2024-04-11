package com.example.nexus.service;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.request.AddMoneyRequest;
import com.example.nexus.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ProfileServiceImplTests {

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddMoney() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getTokenFromRequest(request)).thenReturn("dummyToken");
        when(jwtService.getUsernameFromToken("dummyToken")).thenReturn("testUser");

        Profile profile = new Profile();
        profile.setBalance(100.0F);
        when(profileRepository.findByUserUsername("testUser")).thenReturn(java.util.Optional.of(profile));

        AddMoneyRequest addMoneyRequest = new AddMoneyRequest(50.0F);

        profileService.addMoney(addMoneyRequest, request);

        verify(profileRepository, times(1)).save(profile);
        assert profile.getBalance() == 150.0F;
    }
}