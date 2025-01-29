package com.github.farmplus.repository.order;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of="orderId")
@Builder
@Entity
@Table(name="orders")
public class Order extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "price", nullable = false)
    private Integer price;
    @Column(name = "final_price", nullable = false)
    private Double finalPrice;


}
