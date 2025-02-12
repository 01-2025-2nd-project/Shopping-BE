package com.github.farmplus.repository.notification;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.partyUser.PartyUser;
import com.github.farmplus.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "notification_id")
@Table(name = "notifications")
@Builder
@ToString
public class Notification extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;
    @ManyToOne(fetch = FetchType.EAGER )
    @JoinColumn(name = "user_id",nullable =false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "party_id",nullable =false)
    private Party party;
    @Column(name = "content",nullable = false,length = 255)
    private String content;
    @Column(name = "isRead", nullable = false)
    private Boolean isRead;

    public static Notification from(PartyUser partyUser){
        return Notification.builder()
                .user(partyUser.getUser())
                .isRead(false)
                .content("구매 성사")
                .party(partyUser.getParty())
                .build();
    }
    public void updateIsRead(Boolean isRead){
        this.isRead = isRead;
    }

}
