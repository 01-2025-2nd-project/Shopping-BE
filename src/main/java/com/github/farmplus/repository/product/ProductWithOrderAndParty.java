package com.github.farmplus.repository.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ProductWithOrderAndParty {
    private Product product;
    private Long orderCount;
    private Long partyCount;


}
