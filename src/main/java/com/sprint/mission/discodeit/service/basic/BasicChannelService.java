package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  public ChannelDto create(CreatePublicChannelRequest request) {
    Channel channel = channelMapper.toEntity(
        request.name(),
        ChannelType.PUBLIC,
        request.description());

    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Override
  @Transactional
  public ChannelDto create(CreatePrivateChannelRequest request) {
    Channel channel = channelMapper.toEntity(null, ChannelType.PRIVATE, null);
    request.participantIds().forEach(
        userId -> {
          User user = userRepository.findById(userId)
              .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
          readStatusRepository.save(new ReadStatus(user, channel, Instant.now()));
        }
    );

    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto findById(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    User user = userMapper.toEntity(userService.findById(userId));
    List<UUID> channelIdList = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatus -> readStatus.getChannel().getId())
        .toList();

    List<Channel> channelList = channelRepository.findAll().stream()
        .filter(channel ->
            channel.getChannelType().equals(ChannelType.PUBLIC)
                || channelIdList.contains(channel.getId()))
        .toList();

    return channelList.stream()
        .map(channel -> findById(channel.getId()))
        .toList();
  }

  @Override
  public ChannelDto update(UUID channelId, UpdatePublicChannelRequest updateChannelRequest) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));

    if (channelRepository.existsByName(updateChannelRequest.newName())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 채널 이름입니다.");
    }

    if (channel.getChannelType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("[error] PRIVATE 채널은 수정할 수 없습니다.");
    }

    channel.update(updateChannelRequest.newName(), updateChannelRequest.newDescription());
    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    if (!channelRepository.existsChannelById(channelId)) {
      throw new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다.");
    }
    channelRepository.deleteById(channelId);
  }

}
