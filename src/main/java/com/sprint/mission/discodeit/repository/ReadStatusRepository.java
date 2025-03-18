package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  boolean existsById(UUID statusId);

  Optional<ReadStatus> findById(UUID statusId);

  void deleteByChannelId(UUID channelId);
}
