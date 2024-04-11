package com.example.nexus.specification;

import com.example.nexus.constant.ProductConstants;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductsRequest;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;

public class ProductSpecifications {
    public static Specification<Product> getProductSpecifications(ProductsRequest productsRequest) {
        return findByAvailable()
                .and(findByCampaignName(productsRequest.campaign()))
                .and(findByDiscount(productsRequest.promo()));
    }

    private static Specification<Product> findByAvailable() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get(ProductConstants.AVAILABILITY), 0);
    }

    private static Specification<Product> findByCampaignName(String name) {
        return Optional.ofNullable(name)
                .map(nameValue -> (Specification<Product>) (root, query, criteriaBuilder) -> {
                    root.fetch(ProductConstants.CAMPAIGN, JoinType.INNER);
                    return criteriaBuilder.equal(
                            root.get(ProductConstants.CAMPAIGN)
                                    .get(ProductConstants.NAME),
                            name
                    );
                })
                .orElse(null);
    }

    private static Specification<Product> findByDiscount(Boolean promo) {
        return Optional.ofNullable(promo)
                .filter(Boolean::booleanValue)
                .map(promoValue -> (Specification<Product>) (root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThan(root.get(ProductConstants.DISCOUNT), 0)
                )
                .orElse(null);
    }
}