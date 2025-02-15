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
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements Serializable, MessageService {
    private static final long serialVersionUID = 1L;
    private final Path directory;

    public FileMessageService(Path directory) {
        this.directory = directory;
        FileUtil.init(directory);
    }

    @Override
    public Message create(User sendUser, Channel channel, String messageDetail) {
        Message message = new Message(sendUser, channel, messageDetail);
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);

        // 예외처리
        if (messageDetail == null || messageDetail.isBlank()) {
            throw new IllegalArgumentException("[error] 유효하지 않은 메세지 형식입니다.");
        }
        if (sendUser.getId() == null || sendUser.getId().equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException("[error] 존재하지 않는 사용자는 메세지를 전송할 수 없습니다.");
        }
        if (channel.getId() == null || channel.getId().equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널에서 메세지를 전송할 수 없습니다.");
        }

        messageData.put(message.getId().toString(), message);
        FileUtil.save(directory, messageData);
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        try {
            return messageData.get(messageId.toString());
        } catch(Exception e) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
    }

    @Override
    public List<Message> findAll() {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);
        return messageData.values().stream().toList();
    }

    @Override
    public Message update(UUID messageId, String newDetail) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);

        if (!messageData.containsKey(messageId.toString())) {
            throw new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        Message message = messageData.get(messageId.toString());

        if (newDetail == null || newDetail.isBlank()) {
            throw new IllegalArgumentException("[error] 빈 메세지는 전송할 수 없습니다");
        }
        message.update(newDetail);
        messageData.put(messageId.toString(), message);
        FileUtil.save(directory, messageData);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Map<String, Message> messageData = FileUtil.load(directory, Message.class);

        messageData.remove(messageId.toString());
        FileUtil.save(directory, messageData);
        System.out.println("[삭제 완료]");
    }

}
