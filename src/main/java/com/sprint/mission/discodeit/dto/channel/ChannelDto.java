package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChannelDto(UUID id,
                         String type,
                         String name,
                         String description,
                         List<UUID> participantIds,
                         Instant lastMessageAt) {

  public static ChannelDto fromDomain(Channel channel, Instant lastMessageAt) {
    return ChannelDto.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .participantIds(channel.getParticipantIds())
        .lastMessageAt(lastMessageAt)
        .build();
  }

}
