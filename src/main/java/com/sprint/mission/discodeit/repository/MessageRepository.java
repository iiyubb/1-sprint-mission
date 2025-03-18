package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  boolean existsById(UUID messageId);

  Optional<Message> findById(UUID messageId);

  List<Message> findByChannelId(UUID channelId);

  void deleteById(UUID messageId);
}
