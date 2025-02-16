package discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    private UUID id;
    private Instant createdAt;

    private UUID userId;
    private Instant updatedAt;
    private Instant lastActiveAt;

    // 생성자
    protected UserStatus() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    // Setter
    public void update(Instant newLastActiveAt) {
        if (newLastActiveAt != null && !newLastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = newLastActiveAt;
            this.updatedAt = Instant.now();
        }
    }



}
