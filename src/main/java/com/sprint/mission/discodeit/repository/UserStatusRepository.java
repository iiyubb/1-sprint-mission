package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  boolean existsById(UUID statusId);

  Optional<UserStatus> findById(UUID statusId);

  Optional<UserStatus> findByUserId(UUID userId);

  void deleteByUserId(UUID userId);

}
