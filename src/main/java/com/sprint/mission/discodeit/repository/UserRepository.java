package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsById(UUID userId);

  Optional<User> findById(UUID userId);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  void deleteById(UUID userId);

}