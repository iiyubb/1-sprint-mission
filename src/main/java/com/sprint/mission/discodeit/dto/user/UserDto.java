package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(UUID id,
                      String username,
                      String email,
                      BinaryContent profile,
                      boolean online) {


  public static UserDto fromDomain(User user, boolean isOnline) {
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .profile(user.getProfile())
        .online(isOnline)
        .build();
  }
}