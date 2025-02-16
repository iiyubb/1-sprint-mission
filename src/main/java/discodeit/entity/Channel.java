package discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Channel {
    private UUID id;
    private Instant createdAt;

    private String channelName;
    private ChannelType type;
    private Instant updatedAt;
    private String description;
    private User user;
    private Map<UUID, User> users;

    protected Channel() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public Channel(String channelName, ChannelType type, String description) {
        this();
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
        if (newName != null && !newName.equals(this.channelName)) {
            this.channelName = newName;
            this.updatedAt = Instant.now();
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            this.updatedAt = Instant.now();
        }
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        this.updatedAt = Instant.now();
    }

}