//package discodeit.service.file;
//
//import discodeit.dto.binarycontent.AddBinaryContentRequest;
//import discodeit.dto.message.CreateMessageRequest;
//import discodeit.dto.message.MessageDto;
//import discodeit.dto.message.UpdateMessageRequest;
//import discodeit.entity.Channel;
//import discodeit.entity.Message;
//import discodeit.entity.User;
//import discodeit.service.MessageService;
//import discodeit.service.UserService;
//import discodeit.utils.FileUtil;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class FileMessageService implements MessageService {
//    private final Path path;
//
//    public FileMessageService(Path path) {
//        this.path = path;
//        FileUtil.init(path);
//    }
//
//    @Override
//    public Message create(CreateMessageRequest request, Optional<AddBinaryContentRequest> contentRequest) {
//        Message message = new Message(request.userId(), request.channelId(), request.messageDetail(), Optional.empty());
//        Map<String, Message> messageData = FileUtil.load(path, Message.class);
//
//        // 예외처리
//        if (request.messageDetail() == null || request.messageDetail().isBlank()) {
//            throw new IllegalArgumentException("[error] 유효하지 않은 메세지 형식입니다.");
//        }
//        if (request.userId() == null || request.userId().equals(new UUID(0L, 0L))) {
//            throw new IllegalArgumentException("[error] 존재하지 않는 사용자는 메세지를 전송할 수 없습니다.");
//        }
//        if (request.channelId() == null || request.channelId().equals(new UUID(0L, 0L))) {
//            throw new IllegalArgumentException("[error] 존재하지 않는 채널에서 메세지를 전송할 수 없습니다.");
//        }
//
//        messageData.put(message.getId().toString(), message);
//        FileUtil.save(path, messageData);
//        return message;
//    }
//
//    @Override
//    public Message find(UUID messageId) {
//        Map<String, Message> messageData = FileUtil.load(path, Message.class);
//        try {
//            return messageData.get(messageId.toString());
//        } catch(Exception e) {
//            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
//        }
//    }
//
//    @Override
//    public List<Message> findAllByChannelId(UUID channelId) {
//        Map<String, Message> messageData = FileUtil.load(path, Message.class);
//        return messageData.values().stream()
//                .filter(message -> message.getChannelId().equals(channelId)).toList();
//    }
//
//    @Override
//    public Message update(UUID messageId, UpdateMessageRequest request) {
//        Map<String, Message> messageData = FileUtil.load(path, Message.class);
//
//        if (!messageData.containsKey(messageId.toString())) {
//            throw new NoSuchElementException("[error] 존재하지 않는 메세지 ID입니다.");
//        }
//        Message message = messageData.get(messageId.toString());
//
//        if (request.newMessageDetail() == null || request.newMessageDetail().isBlank()) {
//            throw new IllegalArgumentException("[error] 빈 메세지는 전송할 수 없습니다");
//        }
//        message.update(request.newMessageDetail());
//        messageData.put(messageId.toString(), message);
//        FileUtil.save(path, messageData);
//        return message;
//    }
//
//    @Override
//    public void delete(UUID messageId) {
//        Map<String, Message> messageData = FileUtil.load(path, Message.class);
//
//        messageData.remove(messageId.toString());
//        FileUtil.save(path, messageData);
//        System.out.println("[삭제 완료]");
//    }
//
//}
