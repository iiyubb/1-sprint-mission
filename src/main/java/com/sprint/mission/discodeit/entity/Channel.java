package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
public class Channel {

  private UUID id;
  private Instant createdAt;

  private Instant updatedAt;
  private String type;
  private String name;
  private String description;
  private List<UUID> participantIds;

  protected Channel() {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
  }

  public Channel(String name, String type, String description) {
    this();
    this.name = name;
    this.type = type;
    this.description = description;
    this.participantIds = new ArrayList<>();
  }

  // Setter
  public void update(String newName, String newDescription) {
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
      this.updatedAt = Instant.now();
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
      this.updatedAt = Instant.now();
    }
  }

  public void addParticipant(UUID userId) {
    participantIds.add(userId);
    this.updatedAt = Instant.now();
  }

  public void deleteParticipant(UUID userId) {
    participantIds.remove(userId);
    this.updatedAt = Instant.now();
  }

}