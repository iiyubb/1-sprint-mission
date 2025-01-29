package discodeit.repository.jcf;

import discodeit.entity.Message;
import discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.Map;

public class JCFMessageRepository implements MessageRepository {
    private Map<String, Message> messageData;

    public JCFMessageRepository() {
        this.messageData = new HashMap<>();
    }

    @Override
    public void save(Message message) {
        messageData.put(message.getMessageId(), message);
    }

    @Override
    public Message loadById(String messageId) {
        if (!messageData.containsKey(messageId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        return messageData.get(messageId);
    }

    @Override
    public Map<String, Message> loadAll() {
        return messageData;
    }

    @Override
    public void delete(Message message) {
        messageData.remove(message.getMessageId());
    }
}
