package com.example.nexus.service;

import com.example.nexus.constant.AdminConstants;
import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProfileMapper;
import com.example.nexus.mapper.UserMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.response.ProfileInfoResponse;
import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final ProfileMapper profileMapper;

    @Override
    public void seedAdmin() {
        if(userRepository.findByUsername(AdminConstants.USERNAME).isPresent()) {
            return;
        }

        final var roles = this.roleRepository.findAll();
        final var user = new User();
        final var profile = new Profile();

        user.setUsername(AdminConstants.USERNAME);
        user.setPassword(passwordEncoder.encode(AdminConstants.PASSWORD));
        user.setRoles(roles);
        profile.setUser(user);
        profile.setFirstName(AdminConstants.FIRST_NAME);
        profile.setLastName(AdminConstants.LAST_NAME);

        this.profileRepository.save(profile);
    }

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        return this.userRepository
                .findAll(pageable)
                .map(this.userMapper::userToUserResponse);
    }

    @Override
    public void addUserRole(String username) {
        final var user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageConstants.USER_NOT_FOUND));

        final var desiredRole = this.roleRepository.findByName(RoleConstants.ADMIN)
                .orElseThrow(() -> new NotFoundException(MessageConstants.ROLE_NOT_FOUNT));

        if (user.getRoles().stream().anyMatch(role -> role.getName()
                .equals(desiredRole.getName()))) {
            return;
        }

        user.getRoles().add(desiredRole);

        userRepository.save(user);
    }

    @Override
    public void removeUserRole(String username) {
        final var user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageConstants.USER_NOT_FOUND));

        final var desiredRole = this.roleRepository.findByName(RoleConstants.ADMIN)
                .orElseThrow(() -> new NotFoundException(MessageConstants.ROLE_NOT_FOUNT));

        if (user.getRoles().stream().noneMatch(role -> role.getName()
                .equals(desiredRole.getName()))) {
            return;
        }

        user.getRoles().removeIf(role -> role.getName().equals(RoleConstants.ADMIN));

        userRepository.save(user);
    }

    @Override
    public ProfileInfoResponse getProfileInfo(HttpServletRequest request) {
        final var username = this.getUsernameFromAuthRequest(request);

        final var profile = this.profileRepository.findByUser_Username(username)
                .orElseThrow(() -> new NotFoundException(MessageConstants.PROFILE_NOT_FOUND));

        return this.profileMapper.profileToProfileInfoResponse(profile);
    }

    private String getUsernameFromAuthRequest(HttpServletRequest request) {
        final var token = this.jwtService.getTokenFromRequest(request);

        return this.jwtService.getUsernameFromToken(token);
    }
}