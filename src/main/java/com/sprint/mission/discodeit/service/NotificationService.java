package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<NotificationDto> getNotifications(UUID userId);

  void delete(UUID notificationId, UUID userId);

  void publishNewMessageNotification(UUID channelId, String title, String content);

  void publishRoleChangedNotification(UUID userId, Role role);

  void publishAsyncFailedNotification(UUID userId, String failureMessage);

}
