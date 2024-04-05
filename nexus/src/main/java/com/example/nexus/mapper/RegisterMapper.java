package com.example.nexus.mapper;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegisterMapper {
    @Mapping(target = "user.username", source = "username")
    Profile mapProfile(RegisterRequest registerRequest);
}