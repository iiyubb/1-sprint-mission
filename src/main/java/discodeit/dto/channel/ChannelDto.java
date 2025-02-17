package discodeit.dto.channel;

import discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(UUID id,
                         Instant createdAt,
                         String channelName,
                         ChannelType type,
                         String description,
                         List<UUID> participantIds,
                         Instant lastMessageAt) {
}
