package com.example.nexus.specification;

import com.example.nexus.constant.ProductConstants;
import com.example.nexus.model.entity.Product;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {
    public static Specification<Product> findAvailable() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get(ProductConstants.AVAILABILITY), 0);
    }

    public static Specification<Product> findByCampaignName(String campaignName) {
        return (root, query, criteriaBuilder) -> {
            root.fetch(ProductConstants.CAMPAIGN, JoinType.INNER);
            return criteriaBuilder.equal(
                    root.get(ProductConstants.CAMPAIGN)
                            .get(ProductConstants.NAME),
                    campaignName
            );
        };
    }

    public static Specification<Product> findPromos(Boolean promo) {
        return (root, query, criteriaBuilder) ->
                promo != null && promo ?
                criteriaBuilder.greaterThan(root.get(ProductConstants.DISCOUNT), 0)
                : null;
    }
}