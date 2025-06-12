package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.event.notification.NotificationEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

  private final NotificationRepository notificationRepository;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
  public void handleNotificationEvent(NotificationEvent event) {
    try {
      log.info("알림 이벤트 처리 중: {} (사용자: {})", event.getType(), event.getReceiverId());

      Notification notification = new Notification(event.getReceiverId(),
          event.getType(),
          event.getTargetId(),
          event.getTitle(),
          event.getContent());

      notificationRepository.save(notification);
      log.info("알림이 설공적으로 저장되었습니다: {}", notification.getTitle());
    } catch (Exception e) {
      log.error("알림 이벤트 처리 중 실패했습니다: {}", event, e);
      throw e;
    }
  }

  @Recover
  public void recover(Exception ex, NotificationEvent event) {
    log.error("알림 이벤트의 모든 재시도가 실패했습니다: {}, 오류: {}", event, ex.getMessage());
  }

}
