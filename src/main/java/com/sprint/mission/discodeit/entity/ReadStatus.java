package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "read_statuses")
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @ManyToOne
  @JoinColumn(name = "channel_id", unique = true)
  private Channel channel;

  @Column(nullable = false)
  private Instant lastReadAt;


  public void update() {
    this.lastReadAt = Instant.now();
  }

}
