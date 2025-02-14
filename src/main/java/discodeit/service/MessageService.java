package discodeit.service;

import discodeit.entity.Channel;
import discodeit.entity.Message;

import java.util.List;

public interface MessageService {
    void create(Message newMessage);

    Message readById(String messageId);

    List<Message> readByChannel(String channelId);

    List<Message> readAll();

    Message updateMessage(String messageId, Message updateMessage);

    void delete(String messageId);

    void deleteByChannel(Channel channel);

    Channel getChannel(String messageId);
}