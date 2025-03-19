package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChannelMapper {

  final MessageRepository messageRepository;
  final ReadStatusRepository readStatusRepository;
  final UserMapper userMapper;

  public Channel toEntity(String name, ChannelType channelType, String description) {
    return new Channel(name, channelType, description);
  }

  public ChannelDto toDto(Channel channel) {
    return new ChannelDto(channel.getId(), channel.getChannelType(),
        channel.getName(), channel.getDescription(), getChannelUserList(channel),
        getLastMessageAt(channel.getId()));
  }

  private List<UserDto> getChannelUserList(Channel channel) {
    return channel.isPrivate()
        ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .map(readStatus -> userMapper.toDto(readStatus.getUser()))
        .toList()
        : Collections.emptyList();
  }

  private Instant getLastMessageAt(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId)
        .stream()
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder())
        .orElse(null);
  }
}
