package com.github.farmplus.web.dto.order.response;

import com.github.farmplus.repository.order.Order;
import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class MyOrder {
    private Integer ordersId;
    private double price;
    private String productName;
    private double finalPrice;
    private LocalDate purchaseDate;


    public static MyOrder from(Order order) {
        LocalDate purchaseDate = order.getCreateAt().toLocalDate();
        return MyOrder.builder()
                .ordersId(order.getOrderId()) // orderId는 Integer 타입이므로 long으로 변환
                .price(order.getPrice()) // 가격
                .productName(order.getProduct().getProductName()) // 상품명
                .finalPrice(order.getFinalPrice()) // 최종 가격
                .purchaseDate(purchaseDate)
                .build();
    }

}
