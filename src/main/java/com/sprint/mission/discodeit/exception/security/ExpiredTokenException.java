package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;

public class ExpiredTokenException extends DiscodeitException {

  public ExpiredTokenException() {
    super(ErrorCode.EXPIRED_TOKEN);
  }

  public static ExpiredTokenException withExpiration(Instant expiredAt) {
    ExpiredTokenException exception = new ExpiredTokenException();
    exception.addDetail("expiredAt", expiredAt);
    exception.addDetail("currentTime", Instant.now());
    return exception;
  }

  public static ExpiredTokenException withTokenType(String tokenType) {
    ExpiredTokenException exception = new ExpiredTokenException();
    exception.addDetail("tokenType", tokenType);
    return exception;
  }
}
