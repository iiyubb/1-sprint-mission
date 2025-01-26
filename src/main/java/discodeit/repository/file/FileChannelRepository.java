package discodeit.service.repository.file;

import discodeit.entity.Channel;
import discodeit.service.repository.ChannelRepository;
import discodeit.utils.FileUtil;

import java.nio.file.Path;
import java.util.Map;

public class FileChannelRepository implements ChannelRepository {
    private Map<String, Channel> channelData;
    private Path path;

    public FileChannelRepository() {

    }

    public FileChannelRepository(Path path) {
        this.path = path;
        FileUtil.init(path);
        channelData = FileUtil.load(path, Channel.class);
    }

    @Override
    public void save(Channel channel) {
        channelData.put(channel.getChannelId(), channel);
        FileUtil.save(path, channelData);
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
        FileUtil.save(path, channelData);
    }
}
