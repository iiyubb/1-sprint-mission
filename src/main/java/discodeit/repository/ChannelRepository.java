package discodeit.service.repository;

import discodeit.entity.Channel;

import java.nio.file.Path;
import java.util.Map;

public interface ChannelRepository {
    void save(Channel channel);
    Channel loadById(String channelId);
    Map<String, Channel> loadAll();
    void delete(Channel channel);
}
