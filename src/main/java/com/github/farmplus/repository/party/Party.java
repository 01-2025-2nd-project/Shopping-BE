package com.github.farmplus.repository.party;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.partyUser.PartyUser;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.product_discount.ProductDiscount;
import com.github.farmplus.web.dto.party.request.MakeParty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

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
    private Long partyId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_discount_id", nullable = false)
    private ProductDiscount productDiscount;
    @Column(name = "party_name",nullable = false,length = 255)
    private String partyName;
    @Column(name = "end_date",nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING) // Enum 타입으로 저장
    @Column(name = "status", nullable = false)
    private PartyStatus status;
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    @OneToMany(mappedBy = "party")
    private List<PartyUser> partyUserList;


    public static Party of(Product product, ProductDiscount productDiscount,MakeParty makeParty){
        return Party.builder()
                .product(product)
                .productDiscount(productDiscount)
                .partyName(makeParty.getPartyName())
                .endDate(makeParty.getEndDate())
                .status(PartyStatus.RECRUITING)
                .capacity(makeParty.getPurchaseCount())
                .build();

    }
    public void updateDetails(Product product, ProductDiscount productDiscount, MakeParty makeParty) {
        this.product = product;
        this.productDiscount = productDiscount;
        this.partyName = makeParty.getPartyName();
        this.endDate = makeParty.getEndDate();
        this.status = PartyStatus.RECRUITING;
        this.capacity = makeParty.getPurchaseCount();

    }
    public void updatePartyStatus(PartyStatus partyStatus){
        this.status = partyStatus;
    }
}
