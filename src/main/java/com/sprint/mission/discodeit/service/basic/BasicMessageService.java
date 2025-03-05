package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final UserRepository userRepo;
  private final MessageRepository messageRepo;
  private final ChannelRepository channelRepo;
  private final UserStatusRepository userStatusRepo;
  private final BinaryContentRepository binaryContentRepo;

  @Override
  public Message create(CreateMessageRequest request) {
    // 예외처리
    if (request.content() == null || request.content().isEmpty()) {
      throw new IllegalArgumentException("[error] 유효하지 않은 메세지 형식입니다.");
    }
    if (request.authorId() == null || request.authorId().equals(new UUID(0L, 0L))
        || !userRepo.existsById(request.authorId())) {
      throw new IllegalArgumentException("[error] 존재하지 않는 사용자는 메세지를 전송할 수 없습니다.");
    }
    if (request.channelId() == null || request.channelId().equals(new UUID(0L, 0L))
        || !channelRepo.existsById(request.channelId())) {
      throw new IllegalArgumentException("[error] 존재하지 않는 채널에서 메세지를 전송할 수 없습니다.");
    }
//        if (channelRepo.findAllByUserId(request.authorId()) == null || channelRepo.findAllByUserId(request.authorId()).isEmpty()) {
//            throw new IllegalArgumentException("[error] 메세지 작성자가 해당 채널의 참여자가 아닙니다.");
//        }

    Message message = new Message(request.authorId(), request.channelId(), request.content(),
        null);
    updateUserStatus(userStatusRepo, message.getAuthorId());
    return messageRepo.save(message);
  }

  @Override
  public Message find(UUID messageId) {
    Message message = messageRepo.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));
    updateUserStatus(userStatusRepo, message.getAuthorId());
    return message;
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    channelRepo.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."));
    return messageRepo.findAllByChannelId(channelId).stream().toList();
  }

  @Override
  public Message update(UUID messageId, UpdateMessageRequest request) {
    Message message = messageRepo.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));

    if (request.newContent() == null || request.newContent().isBlank()) {
      throw new IllegalArgumentException("[error] 빈 메세지는 전송할 수 없습니다");
    }

    // 메세지는 작성한 사람만 수정할 수 있음
//        if (!message.getId().equals(request.writerId())) {
//            throw new IllegalArgumentException("[error] 작성자가 아니면 메세지를 수정할 수 없습니다.");
//        }

    message.update(request.newContent());
    updateUserStatus(userStatusRepo, message.getAuthorId());
    return messageRepo.save(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepo.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));

//        if (!message.getId().equals(writerId)) {
//            throw new IllegalArgumentException("[error] 작성자가 아니면 메세지를 수정할 수 없습니다.");
//        }

    message.getAttachmentIds().forEach(binaryContentRepo::deleteById);
    messageRepo.deleteById(messageId);
    updateUserStatus(userStatusRepo, message.getAuthorId());
    System.out.println("[삭제 완료]");
  }

  private void updateUserStatus(UserStatusRepository userStatusRepo, UUID authorId) {
    UserStatus userStatus = userStatusRepo.findByUserId(authorId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
    userStatus.update(Instant.now());
  }

}
