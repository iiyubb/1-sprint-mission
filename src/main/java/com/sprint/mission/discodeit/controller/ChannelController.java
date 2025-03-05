package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final MessageService messageService;

  @PostMapping("/public")
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody CreatePublicChannelRequest request) {
    Channel publicChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(publicChannel);
  }

  @PostMapping("/private")
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody CreatePrivateChannelRequest request) {
    Channel privateChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(privateChannel);
  }

//  @GetMapping("/find/public/{id}")
//  public ResponseEntity<PublicChannelDto> getPublicChannel(@PathVariable("id") UUID channelId) {
//    Channel publicChannel = channelService.find(channelId);
//    Instant lastMessageAt = getLastMessageAt(publicChannel.getId());
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(PublicChannelDto.fromDomain(publicChannel, lastMessageAt));
//  }
//
//  @GetMapping("/find/private/{id}")
//  public ResponseEntity<PrivateChannelDto> getPrivateChannel(@PathVariable("id") UUID channelId) {
//    Channel privateChannel = channelService.find(channelId);
//    Instant lastMessageAt = getLastMessageAt(privateChannel.getId());
//    return ResponseEntity.ok(PrivateChannelDto.fromDomain(privateChannel, lastMessageAt));
//  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> getAllChannelByUserId(
      @RequestParam("userId") UUID userId) {
    List<Channel> channelList = channelService.findAllByUserId(userId);
    System.out.println("channel list size !!!!!" + channelList.size());

    List<ChannelDto> channelDtos = channelList.stream()
        .map(channel -> {
          Instant lastMessageAt = getLastMessageAt(channel.getId());
          return ChannelDto.fromDomain(channel, lastMessageAt);
        }).toList();

    return ResponseEntity.
        status(HttpStatus.OK)
        .body(channelDtos);
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> updateChannel(@PathVariable("channelId") UUID channelId,
      @RequestBody UpdatePublicChannelRequest request) {
    Channel channel = channelService.update(channelId, request);
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

  private Instant getLastMessageAt(UUID channelId) {
    return messageService.findAllByChannelId(channelId)
        .stream()
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .map(Message::getCreatedAt)
        .limit(1)
        .findFirst()
        .orElse(Instant.MIN);
  }

}
