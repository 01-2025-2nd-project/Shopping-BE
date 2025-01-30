package com.github.farmplus.web.dto.product.response;

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
}
