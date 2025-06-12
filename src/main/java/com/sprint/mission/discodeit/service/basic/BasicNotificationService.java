package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.notification.NotificationEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.notification.InvalidNotificationTypeException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.notification.UnsupportedNotificationTypeException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  @Cacheable(value = "userNotifications", key = "#userId")
  public List<NotificationDto> getNotifications(UUID userId) {
    log.debug("캐시에서 사용자 알림 조회 시도: 사용자 ID = {}", userId);
    validateUserId(userId);

    List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(
        userId);

    log.info("사용자 알림 조회 완료: 사용자 ID = {}, 알림 수 = {}", userId, notifications.size());
    return notifications.stream()
        .map(notificationMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @CacheEvict(value = "userNotifications", key = "#userId")
  public void delete(UUID notificationId, UUID userId) {
    log.debug("알림 삭제 및 캐시 무효화: 알림 ID = {}, 사용자 ID = {}", notificationId, userId);
    validateNotificationId(notificationId);
    validateUserId(userId);

    int deletedCount = notificationRepository.deleteByIdAndReceiverId(notificationId, userId);
    if (deletedCount == 0) {
      throw new IllegalArgumentException("알림을 찾을 수 없거나 삭제 권한이 없습니다.");
    }

    log.info("알림이 삭제되었습니다: 사용자 ID = {}", userId);
  }

  @Override
  public void publishNewMessageNotification(UUID channelId, String title, String content) {
    validateChannelId(channelId);

    try {
      List<ReadStatus> enabledUsers = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(
          channelId);

      if (enabledUsers.isEmpty()) {
        log.debug("채널 {}에 알림을 활성화한 사용자가 없습니다.", channelId);
        return;
      }

      for (ReadStatus readStatus : enabledUsers) {
        validateNotificationEvent(NotificationType.NEW_MESSAGE, readStatus.getUser().getId(),
            channelId);

        eventPublisher.publishEvent(NotificationEvent.newMessage(
            readStatus.getUser().getId(),
            channelId,
            "새 메시지: " + title,
            content
        ));
      }

      log.info("채널 {}에 새 메시지 알림이 {}명의 사용자에게 발행되었습니다.", channelId, enabledUsers.size());
    } catch (Exception e) {
      log.error("채널 {}에 새 메시지 알림 발행 중 오류가 발생했습니다.", channelId, e);
      throw new RuntimeException("알림 발행 중 오류가 발생했습니다.", e);
    }
  }

  @Override
  public void publishRoleChangedNotification(UUID userId, Role role) {
    validateUserId(userId);
    validateNotificationEvent(NotificationType.ROLE_CHANGED, userId, userId);

    try {
      eventPublisher.publishEvent(NotificationEvent.roleChanged(
          userId,
          userId,
          "권한이 " + role + "으로 변경되었습니다."
      ));
      log.info("사용자 {}의 권한이 {}로 변경된 알림이 발행되었습니다.", userId, role);
    } catch (Exception e) {
      log.error("사용자 {}의 권한 변경 알림 발행 중 오류가 발생했습니다.", userId, e);
      throw new RuntimeException("권한 변경 알림 발행 중 오류가 발생했습니다.", e);
    }
  }

  @Override
  public void publishAsyncFailedNotification(UUID userId, String failureMessage) {
    validateUserId(userId);
    validateFailureMessage(failureMessage);
    validateNotificationEvent(NotificationType.ASYNC_FAILED, userId, null);

    try {
      eventPublisher.publishEvent(NotificationEvent.asyncFailed(
          userId,
          truncateMessage("비동기 작업 실패: " + failureMessage, 255)
      ));
      log.info("사용자 {}의 비동기 작업 실패 알림이 발행되었습니다.", userId);
    } catch (Exception e) {
      log.error("사용자 {}의 비동기 실패 알림 발행 중 오류가 발생했습니다.", userId, e);
      throw new RuntimeException("비동기 실패 알림 발행 중 오류가 발생했습니다.", e);
    }
  }

  private void validateUserId(UUID userId) {
    if (userId == null || !userRepository.existsById(userId)) {
      throw new UserNotFoundException();
    }
  }

  private void validateNotificationId(UUID notificationId) {
    if (notificationId == null || !notificationRepository.existsById(notificationId)) {
      throw new NotificationNotFoundException();
    }
  }

  private void validateChannelId(UUID channelId) {
    if (channelId == null || !channelRepository.existsById(channelId)) {
      throw new ChannelNotFoundException();
    }
  }

  private void validateFailureMessage(String failure) {
    if (failure == null || failure.trim().isEmpty()) {
      throw new IllegalArgumentException("실패 메시지가 비어있습니다.");
    }
    if (failure.length() > 500) {
      throw new IllegalArgumentException("실패 메시기가 너무 깁니다. (최대 500자)");
    }
  }

  private void validateNotificationEvent(NotificationType type, UUID receiverId, UUID targetId) {
    if (type == null) {
      throw new InvalidNotificationTypeException();
    }

    if (receiverId == null || !userRepository.existsById(receiverId)) {
      throw new UserNotFoundException();
    }

    switch (type) {
      case NEW_MESSAGE:
        if (targetId == null || !channelRepository.existsById(targetId)) {
          throw new ChannelNotFoundException().withId(targetId);
        }
        break;
      case ROLE_CHANGED:
        if (targetId == null || !channelRepository.existsById(targetId)) {
          throw new ChannelNotFoundException().withId(targetId);
        }
        break;
      case ASYNC_FAILED:
        break;
      default:
        throw new UnsupportedNotificationTypeException().wrongType(type);
    }
  }

  private String truncateMessage(String message, int maxLength) {
    if (message == null) {
      return "";
    }
    if (message.length() <= maxLength) {
      return message;
    }
    return message.substring(0, maxLength - 3) + "...";
  }
}
