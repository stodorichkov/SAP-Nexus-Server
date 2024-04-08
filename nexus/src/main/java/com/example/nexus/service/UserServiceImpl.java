package com.example.nexus.service;

import com.example.nexus.constant.AdminConstants;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.User;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
}