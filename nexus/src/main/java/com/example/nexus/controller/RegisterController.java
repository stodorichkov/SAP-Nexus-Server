package com.example.nexus.controller;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.payload.request.RegisterRequest;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/register")
public class RegisterController {

    ProfileRepository profileRepository;
    UserRepository userRepository;
    @PostMapping("/register")
    ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        /*Role role = new Role();
        role.setRole(registerRequest.role());*/

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(registerRequest.password());
        //user.getRoles().add(role);
        userRepository.save(user);

        Profile profile = new Profile();
        profile.setFirstName(registerRequest.firstName());
        profile.setLastName(registerRequest.lastName());
        profile.setBalance(registerRequest.balance());
        profile.setUser(user);
        profileRepository.save(profile);

        return ResponseEntity.ok(profile);
    }
}
