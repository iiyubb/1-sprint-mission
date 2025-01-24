package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.utils.FileUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileMessageService implements Serializable, MessageService {
    private static final long serialVersionUID = 1L;
    private final Path directory;

    public FileMessageService(Path directory) {
        this.directory = directory;
        FileUtil.init(directory);
    }

    @Override
    public void create(Message newMessage) {
        String messageId = newMessage.getMessageId();
        String messageDetail = newMessage.getMessageDetail();
        User sendUser = newMessage.getSendUser();
        User receiveUser = newMessage.getReceiveUser();

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
        if (receiveUser.getUserId() == null || receiveUser.getUserId().isEmpty()) {
            throw new IllegalArgumentException("[error] 존재하지 않는 사용자에게 메세지를 전송할 수 없습니다.");
        }

        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        messageData.put(messageId, newMessage);
        FileUtil.save(directory, messageData);
    }

    @Override
    public Message readById(String messageId) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        if (!messageData.containsKey(messageId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        return messageData.get(messageId);
    }

    @Override
    public List<Message> readByChannel(String channelId) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        List<String> channelList = messageData.values().stream().map(message -> message.getChannel().getChannelId()).toList();
        if (!channelList.contains(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        return messageData.values().stream().filter(message -> message.getChannel().getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public List<Message> readAll() {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        return messageData.values().stream().toList();
    }

    @Override
    public Message updateMessage(String messageId, Message updateMessage) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        if (!messageData.containsKey(messageId)) {
            throw new RuntimeException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        Message originMessage = messageData.get(messageId);

        originMessage.updateMessageDetail(updateMessage.getMessageDetail());
        FileUtil.save(directory, messageData);
        return originMessage;
    }

    @Override
    public void delete(String messageId) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        if (!messageData.containsKey(messageId)) {
            throw new RuntimeException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        messageData.remove(messageId);
        System.out.println("[삭제 완료]");
        FileUtil.save(directory, messageData);
    }

    @Override
    public void deleteByChannel(Channel channel) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        messageData.values().removeIf(message -> message.getChannel().equals(channel));
        FileUtil.save(directory, messageData);
    }

    @Override
    public Channel getChannel(String messageId) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        if (!messageData.containsKey(messageId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        return messageData.get(messageId).getChannel();
    }


    private boolean isMessageIdDuplicate(String messageId) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        return messageData.containsKey(messageId);
    }
}


