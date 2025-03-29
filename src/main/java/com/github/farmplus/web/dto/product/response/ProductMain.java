package com.github.farmplus.web.dto.product.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.farmplus.repository.product.ProductWithOrderAndParty;
import lombok.*;

import java.io.Serializable;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductMain implements Serializable {
    private Long productId;
    private String productName;
    private Long price;
    private Long partyCount;

    public static ProductMain of(ProductWithOrderAndParty productWithOrderCount){
        return ProductMain.builder()
                .productId(productWithOrderCount.getProduct().getProductId())
                .productName(productWithOrderCount.getProduct().getProductName())
                .price(productWithOrderCount.getProduct().getPrice())
                .partyCount(productWithOrderCount.getPartyCount())
                .build();
    }

}
