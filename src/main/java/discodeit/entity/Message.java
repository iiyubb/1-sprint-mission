package discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Message {
    @JsonIgnore
    private UUID id;
    @JsonIgnore
    private Long createdAt;

    private User sendUser;
    private Channel channel;
    private String messageDetail;
    private Long updatedAt;

    // 생성자
    protected Message() {
    }

    protected Message(UUID id, Long createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Message(User sendUser, Channel channel, String messageDetail) {
        this(UUID.randomUUID(), Instant.now().getEpochSecond());
        this.sendUser = sendUser;
        this.channel = channel;
        this.messageDetail = messageDetail;
    }

    // Setter
    public void update(String newDetail) {
        boolean anyValueUpdated = false;
        if (newDetail != null && !newDetail.equals(this.messageDetail)) {
            this.messageDetail = newDetail;
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }

}
