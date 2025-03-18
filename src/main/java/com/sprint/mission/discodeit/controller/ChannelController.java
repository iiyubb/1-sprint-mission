package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannelRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final MessageService messageService;

  @PostMapping("/public")
  public ResponseEntity<ChannelDto> createPublicChannel(
      @RequestBody CreatePublicChannelRequest request) {
    ChannelDto publicChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(publicChannel);
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @RequestBody CreatePrivateChannelRequest request) {
    ChannelDto privateChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(privateChannel);
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> getAllChannelByUserId(
      @RequestParam("userId") UUID userId) {
    List<ChannelDto> channelDtoList = channelService.findAllByUserId(userId);

    return ResponseEntity.
        status(HttpStatus.OK)
        .body(channelDtoList);
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> updateChannel(@PathVariable("channelId") UUID channelId,
      @RequestBody UpdatePublicChannelRequest request) {
    ChannelDto channel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channel);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<String> deleteChannel(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

}
