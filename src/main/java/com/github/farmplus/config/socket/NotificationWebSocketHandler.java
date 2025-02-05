package com.github.farmplus.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler {
    private final SimpMessagingTemplate messagingTemplate;

    // 실시간 알림을 사용자에게 전송하는 메소드
    public void sendNotificationToUser(Integer userId, String message) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", message);
    }

}
