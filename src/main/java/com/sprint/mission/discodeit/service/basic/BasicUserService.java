package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepo;
  private final UserStatusRepository userStatusRepo;
  private final BinaryContentRepository binaryContentRepo;

  @Override
  public User create(CreateUserRequest request) {
    if (isUsernameDuplicate(request.username())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 사용자 이름입니다.");
    }
    if (isEmailDuplicate(request.email())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
    }
    if (isValidEmail(request.email())) {
      throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
    }

    String password = request.password();

    User user = new User(request.username(), request.email(), password, null);
    User createdUser = userRepo.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser, now);
    userStatusRepo.save(userStatus);

    return createdUser;
  }

  @Override
  public User create(CreateUserRequest createUserRequest,
      CreateBinaryContentRequest binaryContentRequest) {
    if (isUsernameDuplicate(createUserRequest.username())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 사용자 이름입니다.");
    }
    if (isEmailDuplicate(createUserRequest.email())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
    }
    if (isValidEmail(createUserRequest.email())) {
      throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
    }

    String fileName = binaryContentRequest.fileName();
    String contentType = binaryContentRequest.contentType();
    byte[] bytes = binaryContentRequest.bytes();

    BinaryContent binaryContent = binaryContentRepo.save(new BinaryContent(fileName, bytes.length,
        contentType, bytes));

    String password = createUserRequest.password();

    User user = new User(createUserRequest.username(), createUserRequest.email(), password,
        binaryContent
    );
    User createdUser = userRepo.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser, now);
    userStatusRepo.save(userStatus);

    return createdUser;
  }

  @Override
  public User find(UUID userId) {
    return userRepo.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
  }

  @Override
  public List<User> findAll() {
    return userRepo.findAll()
        .stream()
        .toList();
  }

  @Override
  public User update(UUID userId, UpdateUserRequest request) {
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

    String newPassword = request.newPassword();
    user.update(newUsername, newEmail, newPassword, null);

    return userRepo.save(user);
  }

  @Override
  public User update(UUID userId, UpdateUserRequest request,
      CreateBinaryContentRequest profileCreateRequest) {
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

    BinaryContent binaryContent = new BinaryContent(profileCreateRequest.fileName(),
        (long) profileCreateRequest.bytes().length,
        profileCreateRequest.contentType(), profileCreateRequest.bytes());

    BinaryContent profile = binaryContentRepo.save(binaryContent);

    String newPassword = request.newPassword();
    user.update(newUsername, newEmail, newPassword, profile);

    return userRepo.save(user);
  }

  @Override
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

}