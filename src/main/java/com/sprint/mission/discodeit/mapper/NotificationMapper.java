package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface NotificationMapper {

  NotificationDto toDto(Notification notification);
}
