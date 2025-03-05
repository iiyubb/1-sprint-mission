package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {

  private UUID id;
  private Instant createdAt;

  private Instant updatedAt;
  private UUID userId;
  private UUID channelId;
  private Instant lastReadAt;

  // 생성자
  protected ReadStatus() {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
  }

  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
    this();
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = lastReadAt;
  }

  // Setter
  public void update(Instant newLastReadAt) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
      this.updatedAt = Instant.now();
    }
  }

}
