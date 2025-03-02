package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record PrivateChannelDto(UUID id,
                                Instant createdAt,
                                String type,
                                List<UUID> participantIds,
                                Instant lastMessageAt) {

  public static PrivateChannelDto fromDomain(Channel channel, Instant lastMessageAt) {
    return PrivateChannelDto.builder()
        .id(channel.getId())
        .createdAt(channel.getCreatedAt())
        .type(channel.getType())
        .lastMessageAt(lastMessageAt)
        .build();
  }
}