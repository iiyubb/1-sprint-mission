//package discodeit.repository.jcf;
//
//import discodeit.entity.Channel;
//import discodeit.repository.ChannelRepository;
//import discodeit.utils.FileUtil;
//
//import java.util.*;
//
//public class JCFChannelRepository implements ChannelRepository {
//    Map<String, Channel> channelData;
//
//    public JCFChannelRepository() {
//        this.channelData = new HashMap<>();
//    }
//
//    public Channel save(Channel channel) {
//        channelData.put(channel.getId().toString(), channel);
//        return channel;
//    }
//
//    @Override
//    public Optional<Channel> findById(UUID channelId) {
//        if (!channelData.containsKey(channelId.toString())) {
//            throw new IllegalArgumentException("[error] 존재하지 않는 channel ID입니다.");
//        }
//        return Optional.ofNullable(channelData.get(channelId.toString()));
//    }
//
//    @Override
//    public List<Channel> findAll() {
//        return channelData.values().stream().toList();
//    }
//
//    @Override
//    public boolean existsById(UUID channelId) {
//        return channelData.containsKey(channelId.toString());
//    }
//
//    @Override
//    public void deleteById(UUID channelId) {
//        channelData.remove(channelId.toString());
//    }
//}
