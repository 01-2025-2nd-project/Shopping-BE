package com.github.farmplus.repository.party;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of="partyId")
@Builder
@Entity
@Table(name="party")
public class Party extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Integer partyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(name = "party_name",nullable = false,length = 255)
    private String partyName;
    @Column(name = "end_date",nullable = false)
    private LocalDate endDate;

}
