package com.github.farmplus.web.dto.order.request;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class OrderRequest {
    private String phoneNumber;
    private Double price;         // 가격이 String 타입인데, double로?
    private Integer quantity;     // 수량 필드 추가
    private Double finalPrice;    // 최종 가격을 Double로 수정
    private String address;
    private Integer totalCount;
    private LocalDate purchaseDate;
}
