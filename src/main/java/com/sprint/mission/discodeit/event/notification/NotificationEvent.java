package com.sprint.mission.discodeit.event.notification;

import com.sprint.mission.discodeit.entity.NotificationType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationEvent {

  private final UUID receiverId;
  private final NotificationType type;
  private final UUID targetId;
  private final String title;
  private final String content;

  public static NotificationEvent newMessage(UUID receiverId, UUID channelId, String title,
      String content) {
    return new NotificationEvent(receiverId, NotificationType.NEW_MESSAGE, channelId, title,
        content);
  }

  public static NotificationEvent roleChanged(UUID receiverId, UUID userId, String details) {
    return new NotificationEvent(receiverId, NotificationType.ROLE_CHANGED, userId, details, null);
  }

  public static NotificationEvent asyncFailed(UUID receiverId, String failureDetails) {
    return new NotificationEvent(receiverId, NotificationType.ASYNC_FAILED, null, null,
        failureDetails);
  }
}

