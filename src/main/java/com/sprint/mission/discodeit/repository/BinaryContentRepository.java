package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

  boolean existsById(UUID contentId);

  Optional<BinaryContent> findById(UUID contentId);

  Optional<BinaryContent> findByMessageId(UUID messageId);

  Optional<BinaryContent> findByUserId(UUID userId);

  List<BinaryContent> findAllByIdIn(List<UUID> contentIds);

  void deleteById(UUID contentId);
}
