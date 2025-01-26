package discodeit.repository.jcf;

import discodeit.entity.Channel;
import discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.Map;

public class JCFChannelRepository implements ChannelRepository {
    Map<String, Channel> channelData;

    public JCFChannelRepository() {
        this.channelData = new HashMap<>();
    }

    @Override
    public void save(Channel channel) {
        channelData.put(channel.getChannelId(), channel);
    }

    @Override
    public Channel loadById(String channelId) {
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 channel ID입니다.");
        }
        return channelData.get(channelId);
    }

    @Override
    public Map<String, Channel> loadAll() {
        return channelData;
    }

    @Override
    public void delete(Channel channel) {
        channelData.remove(channel.getChannelId());
    }

}
