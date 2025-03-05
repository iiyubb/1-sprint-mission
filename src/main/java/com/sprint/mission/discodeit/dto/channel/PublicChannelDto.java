package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record PublicChannelDto(UUID id,
                               Instant createdAt,
                               String channelName,
                               String type,
                               String description,
                               List<UUID> participantIds,
                               Instant lastMessageAt) {

  public static PublicChannelDto fromDomain(Channel channel, Instant lastMessageAt) {
    return PublicChannelDto.builder()
        .id(channel.getId())
        .createdAt(channel.getCreatedAt())
        .channelName(channel.getName())
        .type(channel.getType())
        .description(channel.getDescription())
        .participantIds(channel.getParticipantIds())
        .lastMessageAt(lastMessageAt)
        .build();
  }

}
