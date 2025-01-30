package com.github.farmplus.web.dto.product.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ProductRegister {
    private final String name;
    private final Long price;
    private final Long stock;
    private final String categoryName;
}
