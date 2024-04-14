package com.example.nexus.service;

import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProfileMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.AddMoneyRequest;
import com.example.nexus.model.payload.response.ProfileResponse;
import com.example.nexus.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTests {
    private static Profile profile;
    private static ProfileResponse profileResponse;

    @Mock
    private JwtService jwtService;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private ProfileRepository profileRepository;
    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeAll
    static void setUp() {
        User user = new User();
        user.setUsername("Username");

        profile = new Profile();
        profile.setBalance(100.0F);
        profile.setId(1L);
        profile.setFirstName("firstName");
        profile.setLastName("lastName");
        profile.setUser(user);

        profileResponse = new ProfileResponse(
                "Username",
                "firstName",
                "lastName",
                100.0f
        );
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

    @Test
    public void getProfileInfo_profileNotFound_expectNotFoundException() {
        when(this.jwtService.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn("token");
        when(this.jwtService.getUsernameFromToken(any(String.class))).thenReturn("Username");
        when(this.profileRepository.findByUserUsername("Username")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.profileService
                .getProfileInfo(Mockito.mock(HttpServletRequest.class)));
    }

    @Test
    public void getProfileInfo_everythingIsFine_GetUserInfo() {
        when(this.jwtService.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn("token");
        when(this.jwtService.getUsernameFromToken(any(String.class))).thenReturn("Username");
        when(this.profileRepository.findByUserUsername("Username")).thenReturn(Optional.of(profile));
        when(this.profileMapper.profileToProfileResponse(profile)).thenReturn(profileResponse);

        assertEquals(profileResponse, this.profileService.getProfileInfo(Mockito.mock(HttpServletRequest.class)));
    }
}