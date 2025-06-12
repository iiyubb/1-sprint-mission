package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidNotificationTypeException extends NotificationException {

  public InvalidNotificationTypeException() {
    super(ErrorCode.INVALID_NOTIFICATION_TYPE);
  }
  
}
