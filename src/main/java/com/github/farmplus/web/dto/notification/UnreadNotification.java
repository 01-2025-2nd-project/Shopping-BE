package com.github.farmplus.web.dto.notification;

import com.github.farmplus.repository.notification.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class UnreadNotification {
    private final Long notificationId;
    private final String content;
    private final LocalDateTime createAt;

    public static UnreadNotification from(Notification notification){
        return UnreadNotification.builder()
                .notificationId(notification.getNotificationId())
                .content(notification.getContent())
                .createAt(notification.getCreateAt())
                .build();
    }
}
