package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepo;
  private final UserRepository userRepo;
  private final UserStatusMapper userStatusMapper;
  private final UserRepository userRepository;

  @Override
  public UserStatusDto create(CreateUserStatusRequest request) {
    UUID userId = request.userId();
    if (!userRepo.existsById(userId)) {
      throw new NoSuchElementException("[error] 존재하지 않는 User ID입니다.");
    }
    if (userStatusRepo.findByUserId(userId).isPresent()) {
      throw new IllegalArgumentException("[error] 이미 존재하는 User Status입니다.");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
    UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
    return userStatusMapper.toDto(userStatusRepo.save(userStatus));
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    return userStatusRepo.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User Status ID입니다."));
  }

  @Override
  public UserStatusDto findByUserId(UUID userId) {
    return userStatusRepo.findByUserId(userId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
  }

  @Override
  public List<UserStatusDto> findAll() {

    return userStatusRepo.findAll()
        .stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  public UserStatusDto update(UUID userStatusId, UpdateUserStatusRequest request) {
    UserStatus userStatus = userStatusRepo.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User Status ID입니다."));
    userStatus.update();
    return userStatusMapper.toDto(userStatusRepo.save(userStatus));
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId, UpdateUserStatusRequest request) {
    if (!userRepo.existsById(userId)) {
      throw new NoSuchElementException("[error] 존재하지 않는 User ID입니다.");
    }

    UserStatus userStatus = userStatusRepo.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User Status ID입니다."));
    userStatus.update();
    return userStatusMapper.toDto(userStatusRepo.save(userStatus));
  }

  @Override
  public void delete(UUID userStatusId) {
    if (!userStatusRepo.existsById(userStatusId)) {
      throw new NoSuchElementException("[error] 존재하지 않는 User Status ID입니다.");
    }
    userStatusRepo.deleteById(userStatusId);
  }
}
