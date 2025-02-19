package discodeit.dto.message;

import discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public record CreateMessageRequest(String messageDetail,
                                   UUID userId,
                                   UUID channelId) {
}
