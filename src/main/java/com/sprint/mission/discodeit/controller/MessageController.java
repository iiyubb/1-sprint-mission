package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping
  public ResponseEntity<Message> createMessage(@RequestBody CreateMessageRequest request) {
    Message message = messageService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(message);
  }

  @GetMapping
  public ResponseEntity<List<Message>> getAllMessageByChannelId(
      @RequestParam("channelId") UUID channelId) {
    List<Message> messageList = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messageList);
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> updateMessage(@PathVariable("messageId") UUID messageId,
      @RequestBody UpdateMessageRequest request) {
    Message message = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(message);
  }

  @DeleteMapping(path = "/{messageId}")
  public ResponseEntity<String> deleteMessage(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body("매세지 ID: " + messageId + " delete Complete!!");
  }

}
