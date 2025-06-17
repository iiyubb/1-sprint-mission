package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UnsupportedNotificationTypeException extends NotificationException {

  public UnsupportedNotificationTypeException() {
    super(ErrorCode.UNSUPPORTED_NOTIFICATION_TYPE);
  }

  public static UnsupportedNotificationTypeException wrongType(NotificationType type) {
    UnsupportedNotificationTypeException exception = new UnsupportedNotificationTypeException();
    exception.addDetail("type", type.name());
    return exception;
  }
}
