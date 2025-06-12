package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "async_task_failures")
@RequiredArgsConstructor
@AllArgsConstructor
public class AsyncTaskFailure extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String requestId;

  @Column(nullable = false)
  private String taskType;

  @Column
  private UUID binaryContentId;

  @Column(columnDefinition = "TEXT")
  private String errorMessage;

  @Column
  private Integer retryCount;

}
