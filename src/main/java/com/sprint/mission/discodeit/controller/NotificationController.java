package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.CustomUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements AuthApi {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> find() {
    UUID userId = getCurrentUserId();
    log.debug("알림 조회 요청: 사용자 ID = {}", userId);

    List<NotificationDto> notifications = notificationService.getNotifications(userId);

    log.info("알림 조회 완료: 사용자 = {}, 알림 수 = {}", userId, notifications.size());
    return ResponseEntity.ok(notifications);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> delete(@PathVariable UUID notificationId) {
    UUID userId = getCurrentUserId();
    log.debug("알림 삭제 요청: 알림 ID = {}, 사용자 ID = {}", notificationId, userId);

    notificationService.delete(notificationId, userId);

    log.info("알림 삭제 완료: 알림 ID = {}, 사용자 ID = {}", notificationId, userId);
    return ResponseEntity.noContent().build();
  }

  private UUID getCurrentUserId() {
    return ((CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal()).getUserDto().id();
  }
}
