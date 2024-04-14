package com.example.nexus.mapper;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.response.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {
    @Mapping(target = "username", source = "user.username")
    ProfileResponse profileToProfileResponse(Profile profile);
}
