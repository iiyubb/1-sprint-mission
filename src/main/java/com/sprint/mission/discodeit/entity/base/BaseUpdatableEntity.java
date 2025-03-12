package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  @Column(name = "updatedAt")
  private Instant updatedAt;
}
