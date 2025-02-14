package discodeit.entity;

import java.util.UUID;

public class Message {
    private String messageId;
    private User sendUser;
    private String messageDetail;
    private long createdAt;
    private long updatedAt;
    private Channel channel;

    public Message() {
        messageId = UUID.randomUUID().toString();
        createdAt = System.currentTimeMillis();
    }

    public Message(User sendUser, Channel channel, String messageDetail) {
        this();
        this.sendUser = sendUser;
        this.messageDetail = messageDetail;
        this.channel = channel;
    }

    // Getter
    public String getMessageId() {
        return messageId;
    }

    public User getSendUser() {
        return sendUser;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getMessageDetail() {
        return messageDetail;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    // Setter
    public void updateMessageDetail(String messageDetail) {
        this.messageDetail = messageDetail;
        updatedAt = System.currentTimeMillis();
    }

}
