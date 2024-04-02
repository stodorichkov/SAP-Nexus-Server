package com.example.nexus.mapper;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegisterMapper {
    User mapUser(RegisterRequest registerRequest);
    Profile mapProfile(RegisterRequest registerRequest);
}