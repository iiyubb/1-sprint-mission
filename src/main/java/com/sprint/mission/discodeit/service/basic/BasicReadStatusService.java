package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepo;
  private final UserRepository userRepo;
  private final ChannelRepository channelRepo;
  private final ReadStatusMapper readStatusMapper;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatusDto create(CreateReadStatusRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    if (!userRepo.existsById(userId)) {
      throw new NoSuchElementException("[error] 존재하지 않는 User ID입니다.");
    }
    if (!channelRepo.existsById(channelId)) {
      throw new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다.");
    }
    if (readStatusRepo.findAllByUserId(userId).stream()
        .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channelId))) {
      throw new IllegalArgumentException("[error] 이미 존재하는 Read Status입니다.");
    }

    Instant lastReadAt = request.lastReadAt();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    return readStatusMapper.toDto(readStatusRepo.save(readStatus));
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    return readStatusRepo.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 Read Status ID입니다."));
  }

  @Override
  public List<ReadStatusDto> findAll() {

    return readStatusRepo.findAll()
        .stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepo.findAllByUserId(userId)
        .stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  public ReadStatusDto update(UUID readStatusId, UpdateReadStatusRequest request) {
    ReadStatus readStatus = readStatusRepo.findById(readStatusId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 Read Status ID입니다."));
    readStatus.update();
    return readStatusMapper.toDto(readStatusRepo.save(readStatus));
  }

  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepo.existsById(readStatusId)) {
      throw new NoSuchElementException("[error] 존재하지 않는 Read Status ID입니다.");
    }
    readStatusRepo.deleteById(readStatusId);
  }
}
