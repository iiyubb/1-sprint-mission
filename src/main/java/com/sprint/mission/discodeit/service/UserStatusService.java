package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDto create(CreateUserStatusRequest createUserStatusRequest);

  UserStatusDto find(UUID userStatusId);

  UserStatusDto findByUserId(UUID userId);

  List<UserStatusDto> findAll();

  UserStatusDto update(UUID userStatusId, UpdateUserStatusRequest updateUserStatusRequest);

  UserStatusDto updateByUserId(UUID userId, UpdateUserStatusRequest updateUserStatusRequest);

  void delete(UUID userStatusId);
}
