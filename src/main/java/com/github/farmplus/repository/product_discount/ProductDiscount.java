package com.github.farmplus.repository.product_discount;

import com.github.farmplus.repository.discount.Discount;
import com.github.farmplus.repository.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.relational.core.sql.In;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of="productDiscountId")
@Builder
@Entity
@Table(name="product_discount")
public class ProductDiscount {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_discount_id")
    private Integer productDiscountId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discountId", nullable = false)
    private Discount discount;
}
