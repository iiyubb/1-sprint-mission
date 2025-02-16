package discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Message {
    private UUID id;
    private Instant createdAt;

    private User sendUser;
    private Channel channel;
    private String messageDetail;
    private Instant updatedAt;

    // 생성자
    protected Message() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public Message(User sendUser, Channel channel, String messageDetail) {
        this();
        this.sendUser = sendUser;
        this.channel = channel;
        this.messageDetail = messageDetail;
    }

    // Setter
    public void update(String newDetail) {
        if (newDetail != null && !newDetail.equals(this.messageDetail)) {
            this.messageDetail = newDetail;
            this.updatedAt = Instant.now();
        }
    }

}
