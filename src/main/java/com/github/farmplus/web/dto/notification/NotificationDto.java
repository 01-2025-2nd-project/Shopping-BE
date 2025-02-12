package com.github.farmplus.web.dto.notification;

import com.github.farmplus.repository.notification.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class NotificationDto {
    private Long notificationId;
    private Integer userId;
    private String content;
    private Long partyId;
    private Boolean isRead;
    private String email;
    public static NotificationDto from(Notification notification){
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser().getUserId())
                .content(notification.getContent())
                .partyId(notification.getParty().getPartyId())
                .isRead(notification.getIsRead())
                .email(notification.getUser().getEmail())
                .build();

    }
}
