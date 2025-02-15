package discodeit.service.basic;

import discodeit.entity.Channel;
import discodeit.entity.ChannelType;
import discodeit.entity.User;
import discodeit.repository.UserRepository;
import discodeit.service.ChannelService;
import discodeit.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepo;
    private final UserRepository userRepo;

    @Override
    public Channel create(String name, ChannelType type, String description) {
        Channel channel = new Channel(name, type, description);
        return channelRepo.save(channel);
    }

    @Override
    public Channel find(UUID channelId) {
        return channelRepo.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepo.findAll();
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = channelRepo.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));

        if (isChannelNameDuplicate(newName)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 채널 이름입니다.");
        }

        channel.update(newName, newDescription);
        return channelRepo.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
       channelRepo.deleteById(channelId);
    }

    @Override
    public void addUser(UUID channelId, UUID userId) {
        Channel channel = channelRepo.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));

        if (isUserDuplicate(channel, user.getId())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 user입니다.");
        }
        channel.addUser(user);
        System.out.println("[User 추가 성공]");
        channelRepo.save(channel);
    }

    @Override
    public List<User> findUsers(UUID channelId) {
        Channel channel = channelRepo.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));
        return channel.getUsers().values().stream().toList();
    }

    @Override
    public void deleteUser(UUID channelId, User user) {
        Channel channel = channelRepo.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));

        if (!isUserDuplicate(channel, user.getId())) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 user입니다.");
        }
        channel.getUsers().remove(user.getId());
        System.out.println("[User 삭제 완료]");
        channelRepo.save(channel);
    }


    private boolean isChannelNameDuplicate(String channelName) {
        return channelRepo.findAll().stream().anyMatch(channel -> channel.getChannelName().equals(channelName));
    }

    private boolean isUserDuplicate(Channel channel, UUID userId) {
        return channel.getUser(userId) != null;
    }
}
