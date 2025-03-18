package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageDto create(CreateMessageRequest request, List<MultipartFile> multipartFileList);

  MessageDto findById(UUID messageId);

  PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page);

  MessageDto update(UUID messageId, UpdateMessageRequest updateMessageRequest);

  void delete(UUID messageId);
}