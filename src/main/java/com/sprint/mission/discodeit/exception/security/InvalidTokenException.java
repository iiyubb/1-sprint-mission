package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import jakarta.persistence.criteria.CriteriaBuilder.In;

public class InvalidTokenException extends DiscodeitException {

  public InvalidTokenException() {
    super(ErrorCode.INVALID_TOKEN);
  }

  public static InvalidTokenException withMessage(String message) {
    InvalidTokenException exception = new InvalidTokenException();
    exception.addDetail("message", message);
    return exception;
  }

  public static InvalidTokenException withTokenType(String tokenType) {
    InvalidTokenException exception = new InvalidTokenException();
    exception.addDetail("tokenType", tokenType);
    return exception;
  }

  public static InvalidTokenException withCause(Throwable cause) {
    InvalidTokenException exception = new InvalidTokenException();
    exception.addDetail("cause", cause.getMessage());
    return exception;
  }

  public static InvalidTokenException withMessageAndCause(String message, Throwable cause) {
    InvalidTokenException exception = new InvalidTokenException();
    exception.addDetail("message", message);
    exception.addDetail("cause", cause.getMessage());
    return exception;
  }
}
