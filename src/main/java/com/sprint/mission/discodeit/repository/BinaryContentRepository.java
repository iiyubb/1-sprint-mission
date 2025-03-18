package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

  BinaryContent save(BinaryContent binaryContent);

  boolean existsById(UUID contentId);

  Optional<BinaryContent> findById(UUID contentId);

  List<BinaryContent> findAllByIdIn(List<UUID> contentIds);

  void deleteById(UUID contentId);
}
