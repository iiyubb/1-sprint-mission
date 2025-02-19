//import discodeit.entity.Channel;
//import discodeit.entity.ChannelType;
//import discodeit.entity.Message;
//import discodeit.entity.User;
//import discodeit.factory.BasicServiceFactory;
//import discodeit.factory.ServiceFactory;
//import discodeit.service.ChannelService;
//import discodeit.service.MessageService;
//import discodeit.service.UserService;
//
//import java.nio.file.Path;
//
//
//public class JavaApplication {
//    static User setupUser(UserService userService) {
//        return userService.create("woody", "woody@codeit.com", "010-1111-1111", "woody1234");
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        return channelService.create("공지", ChannelType.PUBLIC, "공지 채널입니다.");
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author, String messageDetail) {
//        Message message = messageService.create(author, channel, messageDetail);
//        System.out.println("메시지 생성: " + message.getMessageDetail());
//    }
//
//    public static void main(String[] args) {
//        Path userPath = Path.of("src/main/java/discodeit/data/User.json");
//        Path channelPath = Path.of("src/main/java/discodeit/data/Channel.json");
//        Path messagePath = Path.of("src/main/java/discodeit/data/Message.json");
//
//        // 서비스 초기화
//        ServiceFactory basicServiceFactory = new BasicServiceFactory(userPath, channelPath, messagePath);
//        UserService userService = basicServiceFactory.createUserService();
//        ChannelService channelService = basicServiceFactory.createChannelService();
//        MessageService messageService = basicServiceFactory.createMessageService();
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user, "안녕하세요.");
//    }
//}
