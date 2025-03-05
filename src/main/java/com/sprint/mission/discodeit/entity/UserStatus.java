package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserStatus {

  private UUID id;
  private Instant createdAt;

  private Instant updatedAt;
  private UUID userId;
  private Instant lastActiveAt;
  private boolean online;

  // 생성자
  protected UserStatus() {
    this.createdAt = Instant.now();
  }

  public UserStatus(UUID userId, Instant lastActiveAt) {
    this();
    this.id = userId;
    this.userId = userId;
    this.lastActiveAt = lastActiveAt;
    this.online = isOnline();
  }

  // Setter
  public void update(Instant newLastActiveAt) {
    if (newLastActiveAt != null && !newLastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = newLastActiveAt;
      this.updatedAt = Instant.now();
      this.online = isOnline();
    }
  }

  public boolean isOnline() {
    return lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
  }

}
