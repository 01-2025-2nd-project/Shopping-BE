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
    private Integer partyUserId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id",nullable = false)
    private Party party;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "party_role",nullable = false)
    private PartyRole partyRole;

}
