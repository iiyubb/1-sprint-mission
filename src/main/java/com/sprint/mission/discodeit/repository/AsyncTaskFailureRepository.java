package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.AsyncTaskFailure;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsyncTaskFailureRepository extends JpaRepository<AsyncTaskFailure, String> {

  List<AsyncTaskFailure> findByRequestId(String requestId);

  List<AsyncTaskFailure> findByBinaryContentId(UUID binaryContentId);

  List<AsyncTaskFailure> findByTaskType(String taskType);

}
