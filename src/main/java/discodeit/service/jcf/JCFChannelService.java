package discodeit.service.jcf;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JCFChannelService implements ChannelService {
    private Map<String, Channel> channelData;
    private MessageService messageService;

    public JCFChannelService(UserService userService, MessageService messageService) {
        channelData = new HashMap<>();
        this.messageService = messageService;
    }

    @Override
    public Channel create(Channel newChannel) {
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

        channelData.put(channelId, newChannel);
        return newChannel;
    }

    @Override
    public Channel readById(String channelId) {
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        return channelData.get(channelId);
    }

    @Override
    public List<Channel> readAll() {
        return channelData.values().stream().toList();
    }

    @Override
    public Channel update(String channelId, Channel updateChannel) {
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        Channel originChannel = channelData.get(channelId);

        if (isChannelNameDuplicate(updateChannel.getChannelName())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 채널 이름입니다.");
        }

        originChannel.updateChannelName(updateChannel.getChannelName());
        return originChannel;
    }

    @Override
    public void deleteChannel(String channelId) {
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }

        channelData.remove(channelId);
        System.out.println("[삭제 완료]");
        messageService.deleteByChannel(channelData.get(channelId));
    }

    @Override
    public void addUser(String channelId, User user) {
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }

        Channel channel = channelData.get(channelId);

        if (isUserDuplicate(channel, user.getUserId())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 user입니다.");
        }
        channel.addUser(user);
        System.out.println("[User 추가 성공]");
    }

    @Override
    public List<User> getUserList(String channelId) {
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }

        Channel channel = channelData.get(channelId);
        return channel.getUsers().values().stream().toList();
    }

    @Override
    public List<Message> getMessageList(String channelId) {
        return messageService.readByChannel(channelId);
    }

    @Override
    public void deleteUser(String channelId, User user) {
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        Channel channel = channelData.get(channelId);

        if (!isUserDuplicate(channel, user.getUserId())) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 user입니다.");
        }
        channel.getUsers().remove(user.getUserId());
        System.out.println("[User 삭제 완료]");
    }


    private boolean isChannelIdDuplicate(String channelId) {
        return channelData.containsKey(channelId);
    }

    private boolean isChannelNameDuplicate(String channelName) {
        return channelData.values().stream().anyMatch(channel -> channel.getChannelName().equals(channelName));
    }

    private boolean isUserDuplicate(Channel channel, String userId) {
        return channel.getUser(userId).getUserId().equals(userId);
    }

}
