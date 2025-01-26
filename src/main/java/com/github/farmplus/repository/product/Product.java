package com.github.farmplus.repository.product;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.category.Category;
import jakarta.persistence.*;
import lombok.*;

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
    private Integer productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;
    @Column(name = "product_name",length = 100,nullable = false)
    private String productName;
    @Column(name = "price",nullable = false)
    private Integer price;
    @Column(name = "stock",nullable = false)
    private Integer stock;

}
