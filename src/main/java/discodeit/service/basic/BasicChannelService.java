package discodeit.service.basic;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.repository.ChannelRepository;
import discodeit.service.repository.MessageRepository;
import discodeit.utils.FileUtil;

import java.util.List;
import java.util.Map;

public class BasicChannelService implements ChannelService {
    private ChannelRepository channelRepo;
    private MessageService messageService;

    public BasicChannelService() {
    }

    public BasicChannelService(ChannelRepository channelRepo, MessageService messageService) {
        this.channelRepo = channelRepo;
        this.messageService = messageService;
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

        channelRepo.save(newChannel);
    }

    @Override
    public Channel readById(String channelId) {
        if (!channelRepo.loadAll().containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        return channelRepo.loadById(channelId);
    }

    @Override
    public List<Channel> readAll() {
        return channelRepo.loadAll().values().stream().toList();
    }

    @Override
    public Channel update(String channelId, Channel updateChannel) {
        Map<String, Channel> channelData = channelRepo.loadAll();
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        Channel originChannel = channelData.get(channelId);

        if (isChannelNameDuplicate(updateChannel.getChannelName())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 채널 이름입니다.");
        }

        originChannel.updateChannelName(updateChannel.getChannelName());
        channelRepo.save(originChannel);
        return originChannel;
    }

    @Override
    public void deleteChannel(String channelId) {
        Map<String, Channel> channelData = channelRepo.loadAll();
        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }

        channelData.remove(channelId);
        System.out.println("[삭제 완료]");
        messageService.deleteByChannel(channelData.get(channelId));
        channelRepo.delete(channelRepo.loadById(channelId));
    }

    @Override
    public void addUser(String channelId, User user) {
        if (!channelRepo.loadAll().containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }

        Channel channel = channelRepo.loadById(channelId);
        if (isUserDuplicate(channel, user.getUserId())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 user입니다.");
        }
        channel.addUser(user);
        System.out.println("[User 추가 성공]");
        channelRepo.save(channel);
    }

    @Override
    public List<User> getUserList(String channelId) {
        if (!channelRepo.loadAll().containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        return channelRepo.loadById(channelId).getUsers().values().stream().toList();
    }

    @Override
    public List<Message> getMessageList(String channelId) {
        return messageService.readByChannel(channelId);
    }

    @Override
    public void deleteUser(String channelId, User user) {
        Map<String, Channel> channelData = channelRepo.loadAll();

        if (!channelData.containsKey(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        Channel channel = channelData.get(channelId);

        if (!isUserDuplicate(channel, user.getUserId())) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 user입니다.");
        }
        channel.getUsers().remove(user.getUserId());
        System.out.println("[User 삭제 완료]");
        channelRepo.save(channel);
    }


    private boolean isChannelIdDuplicate(String channelId) {
        Map<String, Channel> channelData = channelRepo.loadAll();
        return channelData.containsKey(channelId);
    }

    private boolean isChannelNameDuplicate(String channelName) {
        Map<String, Channel> channelData = channelRepo.loadAll();
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
