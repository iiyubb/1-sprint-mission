package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {

  private UUID id;
  private Instant createdAt;

  private String fileName;
  private long size;
  private String contentType;
  private byte[] bytes;

  // 생성자
  protected BinaryContent() {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
  }

  public BinaryContent(String fileName, long size, String contentType, byte[] bytes) {
    this();
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }

}
