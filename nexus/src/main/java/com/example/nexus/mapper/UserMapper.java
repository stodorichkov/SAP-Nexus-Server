package com.example.nexus.mapper;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "username", source = "profile.user.username")
    @Mapping(target = "roles", source = "profile.user.roles")
    UserResponse map(Profile profile);
}