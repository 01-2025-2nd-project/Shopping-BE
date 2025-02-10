package com.github.farmplus.web.dto.order.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class OrderRequest {
    private Integer quantity;     // 수량 필드 추가
}
