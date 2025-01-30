package com.github.farmplus.web.dto.product.response;

import com.github.farmplus.repository.discount.Discount;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ProductOption {
    private final Integer optionId;
    private final Integer option;
    private final Double optionPrice;

    public static ProductOption of(Discount discount){
        return ProductOption.builder()
                .optionId(discount.getDiscountId())
                .option(discount.getPeople())
                .optionPrice(discount.getDiscountRate())
                .build();
    }
}
