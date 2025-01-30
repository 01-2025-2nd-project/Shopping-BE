package com.github.farmplus.web.dto.product.response;

import lombok.*;

@Getter
@ToString
@Builder
public class CategoryResponse {
    private final String name;

    public static CategoryResponse of(String name){
        return CategoryResponse.builder()
                .name(name)
                .build();
    }


}
