package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
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

    UserDto userDto = userService.create(request, profile); // 프로필 없이 사용자 생성

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userDto);
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> findAllUser() {
    List<UserDto> userDtoList = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDtoList);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(@PathVariable("userId") UUID userId,
      @RequestPart UpdateUserRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    UserDto userDto = userService.update(userId, request, profile);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> updateUserStatus(@PathVariable("userId") UUID userId,
      @RequestBody UpdateUserStatusRequest request) {
    UserStatusDto userStatusDto = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userStatusDto);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<String> deleteUser(@PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body("사용자 ID: " + userId + " delete Complete!!");
  }

}
