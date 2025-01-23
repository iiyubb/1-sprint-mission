package discodeit.service.repository.file;

import discodeit.entity.Message;
import discodeit.service.repository.MessageRepository;
import discodeit.utils.FileUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class FileMessageRepository implements MessageRepository {
    private Map<String, Message> messageData;
    private Path path;

    public FileMessageRepository() {
    }

    public FileMessageRepository(Path path) {
        this.path = path;
        FileUtil.init(path);
        this.messageData = FileUtil.load(path, Message.class);
    }


    @Override
    public void save(Message message) {
        messageData.put(message.getMessageId(), message);
        FileUtil.save(path, messageData);
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
        FileUtil.save(path, messageData);
    }


}
