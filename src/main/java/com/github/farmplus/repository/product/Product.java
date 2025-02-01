package com.github.farmplus.repository.product;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.category.Category;
import com.github.farmplus.repository.order.Order;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.web.dto.product.request.ProductRegister;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of="productId")
@Builder
@Entity
@Table(name="product")
public class Product extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productId")
    private Long productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;
    @Column(name = "product_name",length = 100,nullable = false)
    private String productName;
    @Column(name = "price",nullable = false)
    private Long price;
    @Column(name = "stock",nullable = false)
    private Long stock;
    @OneToMany(mappedBy = "product")
    private List<Order> orders;
    @OneToMany(mappedBy = "product")
    private List<Party> parties;

    public static Product of(ProductRegister productRegister,Category category){
        return Product.builder()
                .category(category)
                .productName(productRegister.getName())
                .price(productRegister.getPrice())
                .stock(productRegister.getStock())
                .build();

    }
    public void updateFromRegister(ProductRegister productRegister, Category category) {
        this.productName = productRegister.getName();
        this.price = productRegister.getPrice();
        this.stock = productRegister.getStock();
        this.category = category;
    }
    public void updateStock(Long stock) {
        this.stock =stock;
    }

}
