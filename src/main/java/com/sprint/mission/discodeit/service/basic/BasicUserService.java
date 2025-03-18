package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utils.MultipartFileConverter;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepo;
  private final UserStatusRepository userStatusRepo;
  private final BinaryContentRepository binaryContentRepo;
  private final BinaryContentStorage binaryContentStorage;
  private final MultipartFileConverter multipartFileConverter;
  private final UserMapper userMapper;
  private final UserStatusService userStatusService;


  @Override
  public UserDto create(CreateUserRequest createUserRequest,
      MultipartFile profileFile) {
    if (isUsernameDuplicate(createUserRequest.username())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 사용자 이름입니다.");
    }
    if (isEmailDuplicate(createUserRequest.email())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
    }
    if (isValidEmail(createUserRequest.email())) {
      throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
    }

    User user = new User(createUserRequest.username(), createUserRequest.email(),
        createUserRequest.password());

    updateUserProfile(user, profileFile);
    User savedUser = userRepo.save(user);

    UserStatus savedUserStatus = userStatusRepo.save(new UserStatus(savedUser, Instant.now()));
    savedUser.updateUserStatus(savedUserStatus);

    return userMapper.toDto(savedUser);
  }

  @Override
  public UserDto findById(UUID userId) {
    return userRepo.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
  }

  @Override
  public List<UserDto> findAll() {
    return userRepo.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UpdateUserRequest request,
      MultipartFile multipartFile) {
    User user = userRepo.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = request.newUsername();
    String newEmail = request.newEmail();
    if (isEmailDuplicate(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (isUsernameDuplicate(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    updateUserProfile(user, multipartFile);

    String newPassword = request.newPassword();
    user.update(newUsername, newEmail, newPassword);

    return userMapper.toDto(userRepo.save(user));
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = userRepo.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));

    Optional.ofNullable(user.getProfile())
        .ifPresent(profile -> binaryContentRepo.deleteById(profile.getId()));
    userStatusRepo.deleteByUserId(userId);
    userRepo.deleteById(userId);
    System.out.println("[삭제 완료]");
  }


  private boolean isUsernameDuplicate(String username) {
    return userRepo.findAll().stream().anyMatch(user -> user.getUsername().equals(username));
  }

  private boolean isEmailDuplicate(String email) {
    return userRepo.findAll().stream().anyMatch(user -> user.getEmail().equals(email));
  }

  private boolean isValidEmail(String email) {
    String emailRegExp = "\\w+@\\w+\\.\\w+(\\.\\w+)?";
    return !email.matches(emailRegExp);
  }

  private void updateUserProfile(User user, MultipartFile profileImageFile) {
    Optional.ofNullable(profileImageFile)
        .ifPresent(file -> {
          BinaryContent binaryContent = binaryContentRepo.save(
              BinaryContent.of(file.getOriginalFilename(), file.getSize(), file.getContentType()));

          binaryContentStorage.put(binaryContent.getId(), multipartFileConverter.toByteArray(file));
          user.updateProfile(binaryContent);
        });
  }

}