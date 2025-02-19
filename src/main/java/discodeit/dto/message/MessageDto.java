package discodeit.dto.message;

import discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record MessageDto(UUID id,
                         Instant createdAt,
                         String messageDetail,
                         UUID sendUserId,
                         UUID channelId,
                         List<UUID> attachedIds,
                         Instant updatedAt) {
}
