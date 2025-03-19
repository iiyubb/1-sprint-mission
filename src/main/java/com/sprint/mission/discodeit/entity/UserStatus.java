package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne()
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @NotNull
  private Instant lastActiveAt;

  public void update() {
    this.lastActiveAt = Instant.now();
  }

  public boolean isOnline() {
    return lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
  }

}
