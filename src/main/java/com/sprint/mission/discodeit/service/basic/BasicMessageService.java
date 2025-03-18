package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utils.MultipartFileConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final MultipartFileConverter multipartFileConverter;
  private final PageResponseMapper pageResponseMapper;

  @Override
  public MessageDto create(CreateMessageRequest request, List<MultipartFile> multipartFileList) {
    // 예외처리
    if (request.content() == null || request.content().isEmpty()) {
      throw new IllegalArgumentException("[error] 유효하지 않은 메세지 형식입니다.");
    }
    if (request.author() == null || request.author().equals(new UUID(0L, 0L))
        || !userRepository.existsById(request.author().getId())) {
      throw new IllegalArgumentException("[error] 존재하지 않는 사용자는 메세지를 전송할 수 없습니다.");
    }
    if (request.channel() == null || request.channel().equals(new UUID(0L, 0L))
        || !channelRepository.existsById(request.channel().getId())) {
      throw new IllegalArgumentException("[error] 존재하지 않는 채널에서 메세지를 전송할 수 없습니다.");
    }

    Message message = new Message(request.author(), request.channel(), request.content());

    Optional.ofNullable(multipartFileList)
        .orElse(Collections.emptyList())
        .forEach(multipartFile -> {
          BinaryContent savedContent = binaryContentRepository.save(
              BinaryContent.of(multipartFile.getOriginalFilename(),
                  multipartFile.getSize(),
                  multipartFile.getContentType())
          );

          binaryContentStorage.put(savedContent.getId(),
              multipartFileConverter.toByteArray(multipartFile));
          message.addAttachment(savedContent);
        });

    updateUserStatus(userStatusRepository, message.getAuthor());
    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  public MessageDto findById(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));
    updateUserStatus(userStatusRepository, message.getAuthor());

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page) {

    Pageable pageable = PageRequest.of(page, 50, Sort.by(Sort.Order.desc("createdAt")));

    Page<Message> messagePage = messageRepository.findByChannelId(channelId, pageable);

    return pageResponseMapper.fromPage(messagePage);
  }

  @Override
  public MessageDto update(UUID messageId, UpdateMessageRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));

    if (request.newContent() == null || request.newContent().isBlank()) {
      throw new IllegalArgumentException("[error] 빈 메세지는 전송할 수 없습니다");
    }

    message.update(request.newContent());
    updateUserStatus(userStatusRepository, message.getAuthor());
    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));

    message.getAttachments()
        .forEach(content -> binaryContentRepository.deleteById(content.getId()));
    messageRepository.deleteById(messageId);
    updateUserStatus(userStatusRepository, message.getAuthor());
    System.out.println("[삭제 완료]");
  }

  private void updateUserStatus(UserStatusRepository userStatusRepository, User author) {
    UserStatus userStatus = userStatusRepository.findByUserId(author.getId())
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
    userStatus.update();
  }

}
