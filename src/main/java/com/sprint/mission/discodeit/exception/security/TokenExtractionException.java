package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class TokenExtractionException extends DiscodeitException {

  public TokenExtractionException() {
    super(ErrorCode.TOKEN_EXTRACTION_FAILED);
  }

  public static TokenExtractionException withField(String field) {
    TokenExtractionException exception = new TokenExtractionException();
    exception.addDetail("field", field);
    return exception;
  }

  public static TokenExtractionException withCause(Throwable cause) {
    TokenExtractionException exception = new TokenExtractionException();
    exception.addDetail("cause", cause.getMessage());
    return exception;
  }
}
