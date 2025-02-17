package discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserDto(UUID id,
                      Instant createdAt,
                      String name,
                      String email,
                      String phoneNum,
                      Instant updatedAt,
                      boolean online) {
}
