package discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Channel {
    private UUID id;
    private Long createdAt;

    private String channelName;
    private ChannelType type;
    private Long updatedAt;
    private String description;
    private User user;
    private Map<UUID, User> users;

    protected Channel() {
    }

    protected Channel(UUID id, Long createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Channel(String channelName, ChannelType type, String description) {
        this(UUID.randomUUID(), Instant.now().getEpochSecond());
        this.channelName = channelName;
        this.type = type;
        this.description = description;
        users = new HashMap<>();
    }

    // Getter
    public User getUser(UUID userId) {
        return users.get(userId);
    }

    // Setter
    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.channelName)) {
            this.channelName = newName;
            this.updatedAt = Instant.now().getEpochSecond();
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        this.updatedAt = Instant.now().getEpochSecond();
    }

}