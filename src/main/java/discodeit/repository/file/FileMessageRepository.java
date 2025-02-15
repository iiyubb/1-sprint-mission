package discodeit.repository.file;

import discodeit.entity.Message;
import discodeit.repository.MessageRepository;
import discodeit.utils.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Repository
public class FileMessageRepository implements MessageRepository {
    private Map<String, Message> messageData;
    private final Path path;

    public FileMessageRepository(Path path) {
        this.path = path;
        if (!Files.exists(this.path)) {
            try {
                Files.createFile(this.path);
                FileUtil.save(this.path, new HashMap<>());
            } catch (IOException e) {
                throw new RuntimeException("[error] 메세지 파일을 초기화 불가능", e);
            }
        }
        FileUtil.init(this.path);
        this.messageData = FileUtil.load(this.path, Message.class);
    }


    @Override
    public Message save(Message message) {
        messageData.put(message.getId().toString(), message);
        FileUtil.save(path, messageData);
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
        FileUtil.save(path, messageData);
    }

}
