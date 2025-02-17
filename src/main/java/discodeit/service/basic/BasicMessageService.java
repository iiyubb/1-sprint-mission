package discodeit.service.basic;

import discodeit.dto.binarycontent.AddBinaryContentRequest;
import discodeit.dto.message.CreateMessageRequest;
import discodeit.dto.message.MessageDto;
import discodeit.dto.message.UpdateMessageRequest;
import discodeit.entity.*;
import discodeit.repository.BinaryContentRepository;
import discodeit.repository.ChannelRepository;
import discodeit.service.MessageService;
import discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepo;
    private final ChannelRepository channelRepo;
    private final BinaryContentRepository binaryContentRepo;

    @Override
    public Message create(CreateMessageRequest request, List<AddBinaryContentRequest> contentRequests) {
        // 예외처리
        if (request.messageDetail() == null || request.messageDetail().isEmpty()) {
            throw new IllegalArgumentException("[error] 유효하지 않은 메세지 형식입니다.");
        }
        if (request.userId() == null || request.userId().equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException("[error] 존재하지 않는 사용자는 메세지를 전송할 수 없습니다.");
        }
        if (request.channelId() == null || request.channelId().equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널에서 메세지를 전송할 수 없습니다.");
        }
//        if (channelRepo.findAllByUserId(request.userId()) == null || channelRepo.findAllByUserId(request.userId()).isEmpty()) {
//            throw new IllegalArgumentException("[error] 메세지 작성자가 해당 채널의 참여자가 아닙니다.");
//        }

        List<UUID> attachmentIds = contentRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.filename();
                    BinaryContentType contentType = attachmentRequest.type();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, (long) bytes.length, bytes);
                    BinaryContent createdBinaryContent = binaryContentRepo.save(binaryContent);
                    return createdBinaryContent.getId();
                })
                .toList();

        Message message = new Message(request.userId(), request.channelId(), request.messageDetail(), attachmentIds);
        return messageRepo.save(message);
    }

    @Override
    public MessageDto find(UUID messageId) {
        return messageRepo.findById(messageId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        return messageRepo.findAllByChannelId(channelId).stream().map(this::toDto).toList();
    }

    @Override
    public Message update(UUID messageId, UpdateMessageRequest request) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));

        if (request.newMessageDetail() == null || request.newMessageDetail().isBlank()) {
            throw new IllegalArgumentException("[error] 빈 메세지는 전송할 수 없습니다");
        }
        message.update(request.newMessageDetail());
        return messageRepo.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));

        message.getAttachmentIds().forEach(binaryContentRepo::deleteById);
        messageRepo.deleteById(messageId);
        System.out.println("[삭제 완료]");
    }

    public MessageDto toDto(Message message) {
        List<UUID> attachmentIds = message.getAttachmentIds();
        if (attachmentIds == null) {
            attachmentIds = new ArrayList<>();
        }

        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getMessageDetail(),
                message.getSendUserId(),
                message.getChannelId(),
                attachmentIds,
                message.getUpdatedAt()
        );
    }

}
