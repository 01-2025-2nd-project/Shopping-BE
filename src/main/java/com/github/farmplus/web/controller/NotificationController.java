package com.github.farmplus.web.controller;

import com.github.farmplus.repository.notification.NotificationRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.notification.NotificationService;
import com.github.farmplus.web.dto.base.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    @PutMapping("/read")
    public ResponseDto markNotificationsAsRead(@RequestParam("notification") List<Long> notificationIds) {
        return notificationService.readNotificationsResult(notificationIds);
    }
    @GetMapping("/notifications")
    public ResponseDto getNotifications(@AuthenticationPrincipal CustomUserDetails customUserDetails) {


        return notificationService.getNotificationList(customUserDetails);
    }
}
