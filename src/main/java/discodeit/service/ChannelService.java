package discodeit.service;

import discodeit.entity.Channel;
import discodeit.entity.ChannelType;
import discodeit.entity.Message;
import discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name, ChannelType type, String description);
    Channel find(UUID channelId);
    List<Channel> findAll();
    void addUser(UUID channelId, UUID userId);
    List<User> findUsers(UUID channelId);
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
    void deleteUser(UUID channelId, User user);
}
