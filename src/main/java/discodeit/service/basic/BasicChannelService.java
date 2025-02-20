package discodeit.service.basic;

import discodeit.dto.channel.ChannelDto;
import discodeit.dto.channel.CreatePrivateChannelRequest;
import discodeit.dto.channel.CreatePublicChannelRequest;
import discodeit.dto.channel.UpdateChannelRequest;
import discodeit.entity.*;
import discodeit.repository.MessageRepository;
import discodeit.repository.ReadStatusRepository;
import discodeit.repository.UserRepository;
import discodeit.service.ChannelService;
import discodeit.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepo;
    private final ChannelRepository channelRepo;
    private final MessageRepository messageRepo;
    private final ReadStatusRepository readStatusRepo;

    @Override
    public Channel create(CreatePublicChannelRequest request) {
        Channel channel = new Channel(request.name(), ChannelType.PUBLIC, request.description());
        request.participants().ifPresent(channel::addParticipant);
        channel.getParticipantIds().stream()
                .map(userId -> new ReadStatus(userId, channel.getId(), Instant.MIN))
                .forEach(readStatusRepo::save);
        return channelRepo.save(channel);
    }

    @Override
    public Channel create(CreatePrivateChannelRequest request) {
        Channel channel = new Channel(null, ChannelType.PRIVATE, null);
        channel.addParticipant(request.participant1());
        channel.addParticipant(request.participant2());
        channel.getParticipantIds().stream()
                .map(userId -> new ReadStatus(userId, channel.getId(), Instant.MIN))
                .forEach(readStatusRepo::save);
        return channelRepo.save(channel);
    }

    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepo.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        // private 채널 중 userID가 참여한 채널만 추출
        List<Channel> channelListAll = new ArrayList<>(channelRepo.findAllByUserId(userId)
                .stream()
                .filter(channel -> channel.getType().equals(ChannelType.PRIVATE))
                .filter(channel -> channel.getParticipantIds().contains(userId))
                .toList());

        // 모든 public 채널 추가
        channelRepo.findAll()
                .stream()
                .filter(channel -> channel.getType().equals(ChannelType.PUBLIC))
                .forEach(channelListAll::add);

        return channelListAll.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Channel update(UUID channelId, UpdateChannelRequest updateChannelRequest) {
        Channel channel = channelRepo.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));

        if (isChannelNameDuplicate(updateChannelRequest.newName())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 채널 이름입니다.");
        }

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("[error] PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(updateChannelRequest.newName(), updateChannelRequest.newDescription());
        return channelRepo.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
       channelRepo.deleteById(channelId);
    }

    @Override
    public void addParticipant(UUID channelId, UUID userId) {
        Channel channel = channelRepo.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));

        if (channel.getParticipantIds().contains(userId)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 user입니다.");
        }
        channel.addParticipant(user.getId());
        System.out.println("[User 추가 성공]");
        channelRepo.save(channel);
    }

    @Override
    public void deleteParticipant(UUID channelId, UUID userId) {
        Channel channel = channelRepo.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));

        if (!channel.getParticipantIds().contains(userId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 user입니다.");
        }
        channel.deleteParticipant(userId);
        messageRepo.deleteByChannelId(channelId);
        readStatusRepo.deleteByChannelId(channelId);
        System.out.println("[User 삭제 완료]");
        channelRepo.save(channel);
    }

    public ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepo.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        return new ChannelDto(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getChannelName(),
                channel.getType(),
                channel.getDescription(),
                channel.getParticipantIds(),
                lastMessageAt
        );
    }

    private boolean isChannelNameDuplicate(String channelName) {
        return channelRepo.findAll().stream().anyMatch(channel -> channel.getChannelName().equals(channelName));
    }

    private boolean isUserDuplicate(Channel channel, UUID userId) {
        return channel.getParticipantIds().contains(userId);
    }
}
