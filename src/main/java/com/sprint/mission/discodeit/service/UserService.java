package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserDto create(
      CreateUserRequest createUserRequest,
      MultipartFile multipartFile);

  UserDto findById(UUID userId);

  List<UserDto> findAll();

  UserDto update(
      UUID userId,
      UpdateUserRequest request,
      MultipartFile multipartFile);

  void delete(UUID userId);
}
