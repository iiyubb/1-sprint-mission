package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

  // User
  USER_NOT_FOUND("유저를 찾을 수 없습니다."),
  DUPLICATE_USER("중복된 유저입니다."),

  // Channel
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다.");

  // Message

  private String message;

  ErrorCode(String message) {
    this.message = message;
  }
}
