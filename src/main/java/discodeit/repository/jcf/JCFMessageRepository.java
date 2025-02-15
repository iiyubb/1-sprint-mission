package discodeit.repository.jcf;

import discodeit.entity.Message;
import discodeit.repository.MessageRepository;
import discodeit.utils.FileUtil;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private Map<String, Message> messageData;

    public JCFMessageRepository() {
        this.messageData = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        messageData.put(message.getId().toString(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        if (!messageData.containsKey(messageId.toString())) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        return Optional.ofNullable(messageData.get(messageId.toString()));
    }

    @Override
    public List<Message> findAll() {
        return messageData.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID messageId) {
        return messageData.containsKey(messageId.toString());
    }

    @Override
    public void deleteById(UUID messageId) {
        messageData.remove(messageId.toString());
    }

}
