package discodeit.service.basic;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.MessageService;
import discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepo;

    @Override
    public Message create(User sendUser, Channel channel, String messageDetail) {
        Message message = new Message(sendUser, channel, messageDetail);

        // 예외처리
        if (messageDetail == null || messageDetail.isEmpty()) {
            throw new IllegalArgumentException("[error] 유효하지 않은 메세지 형식입니다.");
        }
        if (sendUser.getId() == null || sendUser.getId().equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException("[error] 존재하지 않는 사용자는 메세지를 전송할 수 없습니다.");
        }
        if (channel.getId() == null || channel.getId().equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널에서 메세지를 전송할 수 없습니다.");
        }

        return messageRepo.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepo.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));
    }

    @Override
    public List<Message> findAll() {
        return messageRepo.findAll();
    }

    @Override
    public Message update(UUID messageId, String newDetail) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다."));

        if (newDetail == null || newDetail.isBlank()) {
            throw new IllegalArgumentException("[error] 빈 메세지는 전송할 수 없습니다");
        }
        message.update(newDetail);
        return messageRepo.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        messageRepo.deleteById(messageId);
        System.out.println("[삭제 완료]");
    }

}
