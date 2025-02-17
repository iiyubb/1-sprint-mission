package discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusRequest(UUID userId,
                                      UUID channelId,
                                      Instant lastReadAt) {
}
