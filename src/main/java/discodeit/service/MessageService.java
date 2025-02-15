package discodeit.service;

import discodeit.entity.User;
import discodeit.entity.Channel;
import discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(User sendUser, Channel channel, String messageDetail);
    Message find(UUID messageId);
    List<Message> findAll();
    Message update(UUID messageId, String newDetail);
    void delete(UUID messageId);
}