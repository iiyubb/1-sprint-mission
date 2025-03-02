package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping
  public ResponseEntity<UserDto> createUser(
      @RequestPart(value = "request") CreateUserRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    if (profile == null || profile.isEmpty()) {
      User user = userService.create(request); // 프로필 없이 사용자 생성
      boolean online = userStatusService.findByUserId(user.getId()).isOnline();

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(UserDto.fromDomain(user, online));
    }

    CreateBinaryContentRequest profileRequest = resolveProfileRequest(profile);

    User user = userService.create(request, profileRequest);
    System.out.println("user 프로필: " + user.getProfileId());// 프로필이 있는 사용자 생성
    boolean online = userStatusService.findByUserId(user.getId()).isOnline();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(UserDto.fromDomain(user, online));
  }

//  @GetMapping("/{id}")
//  public ResponseEntity<UserDto> getUser(@PathVariable("id") UUID userId) {
//    User user = userService.find(userId);
//    boolean online = userStatusService.findByUserId(user.getId())
//        .isOnline();
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(UserDto.fromDomain(user, online));
//  }

  @GetMapping
  public ResponseEntity<List<UserDto>> findAllUser() {
    List<UserDto> userDtoList = userService.findAll().stream()
        .map(user -> {
          boolean online = userStatusService.findByUserId(user.getId()).isOnline();
          return UserDto.fromDomain(user, online);
        })
        .collect(Collectors.toList());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDtoList);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(@PathVariable("userId") UUID userId,
      @RequestPart UpdateUserRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    if (profile == null || profile.isEmpty()) {
      User user = userService.update(userId, request);
      userStatusService.findByUserId(userId).update(Instant.now());
      boolean online = userStatusService.findByUserId(userId)
          .isOnline();
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(UserDto.fromDomain(user, online));
    }

    CreateBinaryContentRequest profileRequest = resolveProfileRequest(profile);

    User user = userService.update(userId, request, profileRequest);
    userStatusService.findByUserId(userId).update(Instant.now());
    boolean online = userStatusService.findByUserId(userId)
        .isOnline();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(UserDto.fromDomain(user, online));
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserDto> updateUserStatus(@PathVariable("userId") UUID userId,
      @RequestBody UpdateUserStatusRequest request) {
    User user = userService.find(userId);
    UserStatus userStatus = userStatusService.findByUserId(userId);
    userStatus.update(request.newLastActiveAt());
    boolean online = userStatusService.findByUserId(user.getId())
        .isOnline();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(UserDto.fromDomain(user, online));
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<String> deleteUser(@PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body("사용자 ID: " + userId + " delete Complete!!");
  }

  private CreateBinaryContentRequest resolveProfileRequest(MultipartFile profileFile) {
    try {
      CreateBinaryContentRequest binaryContentCreateRequest = new CreateBinaryContentRequest(
          profileFile.getOriginalFilename(),
          profileFile.getContentType(),
          profileFile.getBytes()
      );
      return binaryContentCreateRequest;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
