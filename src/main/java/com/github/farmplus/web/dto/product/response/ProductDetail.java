package com.github.farmplus.web.dto.product.response;

import com.github.farmplus.repository.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class ProductDetail {
    private final Long productId;
    private final String productName;
    private final Long price;
    private final String category;
    private final List<ProductOption> productOptions;

    public static ProductDetail of(Product product, List<ProductOption> productOptions){
        return ProductDetail.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .price(product.getPrice())
                    .category(product.getCategory().getCategoryName())
                    .productOptions(productOptions)
                    .build();

    }

}
