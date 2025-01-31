package com.github.farmplus.repository.partyUser;

import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of="partyUserId")
@Builder
@Entity
@Table(name="party_user")
public class PartyUser {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_user_id")
    private Long partyUserId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "party_id",nullable = false)
    private Party party;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "party_role",nullable = false)
    private PartyRole partyRole;
    @Column(name = "payment_amount")
    private Double paymentAmount;

    public static PartyUser host(User user,Party party,Double paymentAmount){
        return PartyUser.builder()
                .party(party)
                .user(user)
                .partyRole(PartyRole.HOST)
                .paymentAmount(paymentAmount)
                .build();
    }
    public void updatePaymentAmount(Double paymentAmount){
        this.paymentAmount = paymentAmount;
    }


}
