package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Message {

  private UUID id;
  private Instant createdAt;

  private Instant updatedAt;
  private String content;
  private UUID channelId;
  private UUID authorId;
  private List<UUID> attachmentIds;

  // 생성자
  protected Message() {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
  }

  public Message(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
    this();
    this.authorId = authorId;
    this.channelId = channelId;
    this.content = content;
    this.attachmentIds = attachmentIds;
  }

  // Setter
  public void update(String newDetail) {
    if (newDetail != null && !newDetail.equals(this.content)) {
      this.content = newDetail;
      this.updatedAt = Instant.now();
    }
  }

}
