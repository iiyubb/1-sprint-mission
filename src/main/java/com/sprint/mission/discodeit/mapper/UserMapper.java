package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "username", source = "username")
  User toEntity(UserDto userDto);

  @Mapping(target = "username", source = "username")
  @Mapping(target = "profile", source = "profile")
  @Mapping(target = "online", expression = "java(user.getUserStatus().isOnline())")
  UserDto toDto(User user);

}
