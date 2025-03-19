package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = MessageMapper.class)
public interface PageResponseMapper {

  PageResponseMapper INSTANCE = Mappers.getMapper(PageResponseMapper.class);

  @Mapping(target = "content", source = "content")
  PageResponse<MessageDto> fromPage(Page<Message> page);
}