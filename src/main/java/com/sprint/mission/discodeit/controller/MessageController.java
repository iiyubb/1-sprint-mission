package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<MessageDto> createMessage(
      @RequestPart(value = "messageCreateRequest") CreateMessageRequest createMessageRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> multipartFileList) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageService.create(createMessageRequest, multipartFileList));
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<MessageDto> updateMessage(@PathVariable UUID id,
      @RequestBody UpdateMessageRequest updateMessageRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(messageService.update(id, updateMessageRequest));
  }

  @DeleteMapping(value = "{id}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
    messageService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> getAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @RequestParam(value = "page", defaultValue = "0") int page) {
    PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, page);
    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }
}