package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.ChannelType;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.utils.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements Serializable, ChannelService {
    private static final long serialVersionUID = 1L;
    private final Path directory;
    private final Path userDirectory;

    public FileChannelService(Path directory, Path userDirectory, MessageService messageService) {
        this.directory = directory;
        this.userDirectory = userDirectory;
        FileUtil.init(directory);
        FileUtil.init(userDirectory);
    }

    @Override
    public Channel create(String name, ChannelType type, String description) {
        Channel channel = new Channel(name, type, description);
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        channelData.put(channel.getId().toString(), channel);
        FileUtil.save(directory, channelData);
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        try {
            return channelData.get(channelId.toString());
        } catch(Exception e) {
            throw new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다.");
        }
    }

    @Override
    public List<Channel> findAll() {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        return channelData.values().stream().toList();
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        Channel channel = channelData.get(channelId.toString());

        if (isChannelNameDuplicate(newName)) {
            throw new NoSuchElementException("[error] 이미 존재하는 채널 이름입니다.");
        }

        channel.update(newName, newDescription);
        channelData.put(channel.getId().toString(), channel);
        FileUtil.save(directory, channelData);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        channelData.remove(channelData.toString());
        FileUtil.save(directory, channelData);
    }

    @Override
    public void addUser(UUID channelId, UUID userId) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        Map<String, User> userData = FileUtil.load(userDirectory, User.class);
        Channel channel = channelData.get(channelId.toString());
        if (!userData.containsKey(userId.toString())) {
            throw new NoSuchElementException("[error] 존재하지 않는 user입니다.");
        }
        User user = userData.get(userId.toString());

        if (isUserDuplicate(channel, user.getId())) {
            throw new NoSuchElementException("[error] 이미 존재하는 user입니다.");
        }
        channel.addUser(user);
        System.out.println("[User 추가 성공]");
        channelData.put(channelId.toString(), channel);
        FileUtil.save(directory, channelData);
    }

    @Override
    public List<User> findUsers(UUID channelId) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        Channel channel = channelData.get(channelId.toString());
        return channel.getUsers().values().stream().toList();
    }

    @Override
    public void deleteUser(UUID channelId, User user) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);

        Channel channel = channelData.get(channelId.toString());

        if (!isUserDuplicate(channel, user.getId())) {
            throw new NoSuchElementException("[error] 존재하지 않는 채널 user입니다.");
        }
        channel.getUsers().remove(user.getId());
        System.out.println("[User 삭제 완료]");
        channelData.put(channelId.toString(), channel);
        FileUtil.save(directory, channelData);
    }


    private boolean isChannelNameDuplicate(String channelName) {
        Map<String, Channel> channelData = FileUtil.load(directory, Channel.class);
        return channelData.values().stream().anyMatch(channel -> channel.getChannelName().equals(channelName));
    }

    private boolean isUserDuplicate(Channel channel, UUID userId) {
        return channel.getUser(userId) != null;
    }
}
