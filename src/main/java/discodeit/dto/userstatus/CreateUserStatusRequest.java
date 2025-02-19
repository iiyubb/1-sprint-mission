package discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record CreateUserStatusRequest(UUID userId,
                                      Instant lastActiveAt) {
}
