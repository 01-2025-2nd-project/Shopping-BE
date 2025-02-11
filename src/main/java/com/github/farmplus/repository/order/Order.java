package com.github.farmplus.repository.order;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.partyUser.PartyUser;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.web.dto.order.request.OrderRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor

@EqualsAndHashCode(of="orderId")
@Builder
@Entity
@Table(name="orders")
public class Order extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "party_id")
    private Party party;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "price", nullable = false)
    private Long price;
    @Column(name = "final_price", nullable = false)
    private Double finalPrice;
    @Enumerated(EnumType.STRING)

    public static Order from(PartyUser partyUser){
        return Order.builder()
                .user(partyUser.getUser())
                .product(partyUser.getParty().getProduct())
                .party(partyUser.getParty())
                .quantity(partyUser.getParty().getCapacity())
                .price(partyUser.getParty().getCapacity() * partyUser.getParty().getProduct().getPrice())
                .finalPrice(partyUser.getPaymentAmount())
                .build();
    }

    public static Order of(User user, Product product, OrderRequest orderRequest){
        return Order.builder()
                .user(user)
                .product(product)
                .party(null)
                .quantity(orderRequest.getQuantity())
                .price(product.getPrice() * orderRequest.getQuantity())
                .finalPrice((double) (product.getPrice() * orderRequest.getQuantity()))
                .build();
    }


    public void updateParty(Party party){
        this.party = party;
    }


}

