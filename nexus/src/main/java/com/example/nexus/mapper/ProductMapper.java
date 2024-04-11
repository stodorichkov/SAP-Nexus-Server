package com.example.nexus.mapper;

import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    Product productRequestToProduct(ProductRequest productRequest);

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "campaign", source = "campaign.name")
    ProductResponse productToProductResponse(Product product);
}