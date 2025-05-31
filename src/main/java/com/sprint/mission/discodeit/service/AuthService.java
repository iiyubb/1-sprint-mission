package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.basic.BasicAuthService.TokenResponse;
import java.util.UUID;

public interface AuthService {

  UserDto initAdmin();

  TokenResponse login(UUID userId);

  void logout(String accessToken);

  UserDto updateRole(UserRoleUpdateRequest request);

  boolean isUserLoggedIn(UUID userId);

  TokenResponse refreshToken(String refreshToken);

  int getActiveSessionCount(UUID userId);

}
