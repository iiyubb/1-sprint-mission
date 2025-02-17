package discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Message {
    private UUID id;
    private Instant createdAt;

    private String messageDetail;
    private UUID sendUserId;
    private UUID channelId;
    private List<UUID> attachmentIds;
    private Instant updatedAt;

    // 생성자
    protected Message() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public Message(UUID sendUserId, UUID channelId, String messageDetail, List<UUID> attachmentIds) {
        this();
        this.sendUserId = sendUserId;
        this.channelId = channelId;
        this.messageDetail = messageDetail;
        this.attachmentIds = attachmentIds;
    }

    // Setter
    public void update(String newDetail) {
        if (newDetail != null && !newDetail.equals(this.messageDetail)) {
            this.messageDetail = newDetail;
            this.updatedAt = Instant.now();
        }
    }

}
