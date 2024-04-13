package com.example.nexus.service;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.request.AddMoneyRequest;
import com.example.nexus.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTests {
    private static Profile profile;

    @Mock
    private JwtService jwtService;
    @Mock
    private ProfileRepository profileRepository;
    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeAll
    static void setUp() {
        profile = new Profile();
        profile.setBalance(100.0F);
    }

    @Test
    void addMoney() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getTokenFromRequest(request)).thenReturn("dummyToken");
        when(jwtService.getUsernameFromToken("dummyToken")).thenReturn("testUser");
        when(profileRepository.findByUserUsername("testUser")).thenReturn(java.util.Optional.of(profile));

        AddMoneyRequest addMoneyRequest = new AddMoneyRequest(50.0F);

        profileService.addMoney(addMoneyRequest, request);

        verify(profileRepository, times(1)).save(profile);

        assertEquals(150.0F, profile.getBalance());
    }
}