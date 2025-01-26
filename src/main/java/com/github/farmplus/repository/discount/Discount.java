package com.github.farmplus.repository.discount;

import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of="discountId")
@Builder
@Entity
@Table(name="discount")
public class Discount {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Integer discountId;
    @Column(name = "people", nullable = false)
    private Integer people;
    @Column(name = "discount_rate", nullable = false)
    private Double discountRate;
}
