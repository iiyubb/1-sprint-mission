package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class InvalidRefreshTokenException extends DiscodeitException {

  public InvalidRefreshTokenException() {
    super(ErrorCode.INVALID_REFRESH_TOKEN);
  }

  public static InvalidRefreshTokenException withUserId(UUID userId) {
    InvalidRefreshTokenException exception = new InvalidRefreshTokenException();
    exception.addDetail("userId", userId);
    return exception;
  }

  public static InvalidRefreshTokenException notFound() {
    InvalidRefreshTokenException exception = new InvalidRefreshTokenException();
    exception.addDetail("reason", "refresh token not found in database");
    return exception;
  }

}
