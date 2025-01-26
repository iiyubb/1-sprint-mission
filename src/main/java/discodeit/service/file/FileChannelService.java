package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.utils.FileUtil;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FileChannelService implements Serializable, ChannelService {
    private static final long serialVersionUID = 1L;
    private MessageService messageService;
    private final Path directory;


    public FileChannelService(Path directory, MessageService messageService) {
        this.directory = directory;
        this.messageService = messageService;
        FileUtil.init(directory);
    }

    @Override
    public void create(Channel newChannel) {
        String channelId = newChannel.getChannelId();
        String channelName = newChannel.getChannelName();

        if (isChannelIdDuplicate(channelId)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 채널 ID입니다.");
        }
        if (channelName == null || channelName.isEmpty()) {
            throw new IllegalArgumentException("[error] 유효하지 않은 채널 이름입니다.");
        }
        if (isChannelNameDuplicate(channelName)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 채널 이름입니다.");
        }

        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        channelData.put(channelId, newChannel);
        FileUtil.save(directory, channelData);
    }

    @Override
    public Channel readById(String channelId) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        return channelData.get(channelId);
    }

    @Override
    public List<Channel> readAll() {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        return channelData.values().stream().toList();
    }

    @Override
    public Channel update(String channelId, Channel updateChannel) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        Channel originChannel = channelData.get(channelId);

        if (isChannelNameDuplicate(updateChannel.getChannelName())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 채널 이름입니다.");
        }

        originChannel.updateChannelName(updateChannel.getChannelName());
        FileUtil.save(directory, channelData);
        return originChannel;
    }

    @Override
    public void deleteChannel(String channelId) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }

        channelData.remove(channelId);
        System.out.println("[삭제 완료]");
        messageService.deleteByChannel(channelData.get(channelId));
        FileUtil.save(directory, channelData);
    }

    @Override
    public void addUser(String channelId, User user) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);

        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }

        Channel channel = channelData.get(channelId);
        if (isUserDuplicate(channel, user.getUserId())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 user입니다.");
        }
        channel.addUser(user);
        System.out.println("[User 추가 성공]");
        FileUtil.save(directory, channelData);
    }

    @Override
    public List<User> getUserList(String channelId) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }

        Channel channel = channelData.get(channelId);
        return channel.getUsers().values().stream().toList();
    }

    @Override
    public void deleteUser(String channelId, User user) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);

        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        Channel channel = channelData.get(channelId);

        if (!isUserDuplicate(channel, user.getUserId())) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 user입니다.");
        }
        channel.getUsers().remove(user.getUserId());
        System.out.println("[User 삭제 완료]");
        FileUtil.save(directory, channelData);
    }


    private boolean isChannelIdDuplicate(String channelId) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        return channelData.containsKey(channelId);
    }

    private boolean isChannelNameDuplicate(String channelName) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        return channelData.values().stream().anyMatch(channel -> channel.getChannelName().equals(channelName));
    }

    private boolean isUserDuplicate(Channel channel, String userId) {
        if (channel.getUser(userId) == null) {
            return false;
        } else {
            return channel.getUser(userId).getUserId().equals(userId);
        }
    }
}
