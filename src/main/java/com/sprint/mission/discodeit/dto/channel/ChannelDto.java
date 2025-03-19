package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChannelDto(UUID id,
                         ChannelType type,
                         String name,
                         String description,
                         List<UserDto> participants,
                         Instant lastMessageAt) {

  public ChannelDto fromDomain(Channel channel, Instant lastMessageAt) {
    return ChannelDto.builder()
        .id(channel.getId())
        .type(channel.getChannelType())
        .name(channel.getName())
        .description(channel.getDescription())
        .lastMessageAt(lastMessageAt)
        .participants(participants)
        .build();
  }

}
