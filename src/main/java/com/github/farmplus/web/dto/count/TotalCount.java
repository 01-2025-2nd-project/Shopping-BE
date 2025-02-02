package com.github.farmplus.web.dto.count;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class TotalCount {
    private final Integer totalSize;
    public static TotalCount of(Integer totalSize){
        return TotalCount.builder()
                .totalSize(totalSize)
                .build();
    }
}
