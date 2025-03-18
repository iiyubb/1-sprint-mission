package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  boolean existsChannelById(UUID channelId);

  boolean existsByName(String name);

  Optional<Channel> findById(UUID channelId);

  void deleteById(UUID channelId);
}
