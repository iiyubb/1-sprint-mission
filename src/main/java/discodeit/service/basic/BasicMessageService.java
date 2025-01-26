package discodeit.service.basic;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.MessageService;
import discodeit.repository.MessageRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicMessageService implements MessageService {
    private MessageRepository messageRepo;

    public BasicMessageService() {
    }

    public BasicMessageService(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    @Override
    public void create(Message newMessage) {
        String messageId = newMessage.getMessageId();
        String messageDetail = newMessage.getMessageDetail();
        User sendUser = newMessage.getSendUser();
        Channel channel = newMessage.getChannel();

        // 예외처리
        if (isMessageIdDuplicate(messageId)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 메세지 ID입니다.");
        }
        if (messageDetail == null || messageDetail.isEmpty()) {
            throw new IllegalArgumentException("[error] 유효하지 않은 메세지 형식입니다.");
        }
        if (sendUser.getUserId() == null || sendUser.getUserId().isEmpty()) {
            throw new IllegalArgumentException("[error] 존재하지 않는 사용자는 메세지를 전송할 수 없습니다.");
        }
        if (channel.getChannelId() == null || channel.getChannelId().isEmpty()) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널에서 메세지를 전송할 수 없습니다.");
        }

        messageRepo.save(newMessage);
    }

    @Override
    public Message readById(String messageId) {
        Map<String, Message> messageData = messageRepo.loadAll();
        if (!messageData.containsKey(messageId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        return messageData.get(messageId);
    }

    @Override
    public List<Message> readByChannel(String channelId) {
        Map<String, Message> messageData = messageRepo.loadAll();
        List<String> channelList = messageData.values().stream().map(m -> m.getChannel().getChannelId()).toList();
        if (!channelList.contains(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        return messageData.values().stream().filter(message -> message.getChannel().getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public List<Message> readAll() {
        return messageRepo.loadAll().values().stream().toList();
    }

    @Override
    public Message updateMessage(String messageId, Message updateMessage) {
        Map<String, Message> messageData = messageRepo.loadAll();
        if (!messageData.containsKey(messageId)) {
            throw new RuntimeException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        Message originMessage = messageData.get(messageId);

        originMessage.updateMessageDetail(updateMessage.getMessageDetail());
        messageRepo.save(originMessage);
        return originMessage;
    }

    @Override
    public void delete(String messageId) {
        Map<String, Message> messageData = messageRepo.loadAll();
        if (!messageData.containsKey(messageId)) {
            throw new RuntimeException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        messageRepo.delete(messageRepo.loadById(messageId));
        System.out.println("[삭제 완료]");
    }

    @Override
    public void deleteByChannel(Channel channel) {
        messageRepo.loadAll().values().removeIf(m -> m.getChannel().equals(channel));
    }

    @Override
    public Channel getChannel(String messageId) {
        Map<String, Message> messageData = messageRepo.loadAll();
        if (!messageData.containsKey(messageId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        return messageData.get(messageId).getChannel();
    }


    private boolean isMessageIdDuplicate(String messageId) {
        return messageRepo.loadAll().containsKey(messageId);
    }
}
