package com.example.nexus.specification;

import com.example.nexus.constant.ProductConstants;
import com.example.nexus.model.entity.Product;
import com.example.nexus.model.payload.request.ProductsRequest;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

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
        return (root, query, criteriaBuilder) -> {
          root.fetch(ProductConstants.CAMPAIGN, JoinType.INNER);
          return criteriaBuilder.equal(
                  root.get(ProductConstants.CAMPAIGN)
                          .get(ProductConstants.NAME),
                  name
          );
        };
    }

    private static Specification<Product> findByDiscount(Boolean promo) {
        if(!promo) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get(ProductConstants.DISCOUNT), 0);
    }
}