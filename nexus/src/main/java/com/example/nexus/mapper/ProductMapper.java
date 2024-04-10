package com.example.nexus.mapper;

import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    Product productRequestToProduct(ProductRequest productRequest);
}