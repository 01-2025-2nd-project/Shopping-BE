package com.github.farmplus.web.dto.product.response;

import com.github.farmplus.repository.product.ProductWithOrderAndParty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ProductMain {
    private final Long productId;
    private final String productName;
    private final Long price;
    private final Long partyCount;

    public static ProductMain of(ProductWithOrderAndParty productWithOrderCount){
        return ProductMain.builder()
                .productId(productWithOrderCount.getProduct().getProductId())
                .productName(productWithOrderCount.getProduct().getProductName())
                .price(productWithOrderCount.getProduct().getPrice())
                .partyCount(productWithOrderCount.getPartyCount())
                .build();
    }

}
