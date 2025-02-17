package discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
public class Channel {
    private UUID id;
    private Instant createdAt;

    private String channelName;
    private ChannelType type;
    private Instant updatedAt;
    private String description;
    private List<UUID> participantIds;

    protected Channel() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public Channel(String channelName, ChannelType type, String description) {
        this();
        this.channelName = channelName;
        this.type = type;
        this.description = description;
        participantIds = new ArrayList<>();
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

    public void addParticipant(UUID userId) {
        participantIds.add(userId);
        this.updatedAt = Instant.now();
    }

    public void deleteParticipant(UUID userId) {
        participantIds.remove(userId);
        this.updatedAt = Instant.now();
    }

}