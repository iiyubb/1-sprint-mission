package discodeit.repository;


import discodeit.entity.Message;

import java.nio.file.Path;
import java.util.Map;

public interface MessageRepository {
    void save(Message message);

    Message loadById(String messageId);

    Map<String, Message> loadAll();

    void delete(Message message);
}
