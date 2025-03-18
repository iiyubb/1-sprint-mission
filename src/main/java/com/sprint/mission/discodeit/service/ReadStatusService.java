package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusDto create(CreateReadStatusRequest createReadStatusRequest);

  ReadStatusDto find(UUID readStatusId);

  List<ReadStatusDto> findAll();

  List<ReadStatusDto> findAllByUserId(UUID userId);

  ReadStatusDto update(UUID userId, UpdateReadStatusRequest updateReadStatusRequest);

  void delete(UUID userId);
}
