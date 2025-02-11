package com.github.farmplus.service.notification;

import com.github.farmplus.repository.notification.Notification;
import com.github.farmplus.repository.notification.NotificationRepository;
import com.github.farmplus.repository.party.PartyRepository;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.exceptions.NotFoundException;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.notification.UnreadNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PartyRepository partyRepository;

    @Transactional
    @CacheEvict(value = "notificationList", key = "#customUserDetails.userId")
    public ResponseDto readNotificationsResult(List<Long> notificationIds,CustomUserDetails customUserDetails) {
        User user = userRepository.findByEmailFetchJoin(customUserDetails.getEmail())
                .orElseThrow(()->new NotFoundException(customUserDetails.getEmail() + "에 해당하는 유저를 찾을 수 없습니다."));
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        notifications.forEach(notification -> notification.updateIsRead(true));  // 알림 읽음 처리
        notificationRepository.saveAll(notifications);
        return new ResponseDto(HttpStatus.OK.value(), "알림들이 읽음 처리되었습니다.");

    }

    @Cacheable(value = "notificationList",key = "#customUserDetails.userId")
    public ResponseDto getNotificationList(CustomUserDetails customUserDetails) {
        User user = userRepository.findByEmailFetchJoin(customUserDetails.getEmail())
                .orElseThrow(()-> new NotFoundException("해당 유저를 찾을 수 없습니다."));
        List<Notification> notifications = notificationRepository.findAllByUserAndIsRead(user, false);  // 읽지 않은 알림들
        List<UnreadNotification> unreadNotifications = notifications.stream().map(UnreadNotification::from).toList();
        return new ResponseDto(HttpStatus.OK.value(), "안 읽은 알림 리스트 조회 서공",unreadNotifications);
    }
}
