package com.example.nexus.mapper;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.payload.request.RegisterRequest;

public class RegisterMapper {

    User getMappedUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(registerRequest.password());
        return user;
    }

    /*Profile getMappedProfile(RegisterRequest registerRequest) {
        Profile profile = new Profile();
        profile.setFirstName(registerRequest.firstName());
        profile.setLastName(registerRequest.lastName());
        profile.setBalance(registerRequest.balance());
    }*/
}
