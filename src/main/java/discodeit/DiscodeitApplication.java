package discodeit;

import discodeit.entity.Channel;
import discodeit.entity.ChannelType;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Path;

@SpringBootApplication
public class DiscodeitApplication {

    static User setupUser(UserService userService) {
        return userService.create("woody", "woody@codeit.com", "010-1111-1111", "woody1234");
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.create("공지", ChannelType.PUBLIC, "공지 채널입니다.");
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author, String messageDetail) {
        Message message = messageService.create(author, channel, messageDetail);
        System.out.println("메시지 생성: " + message.getMessageDetail());
    }

    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 서비스 초기화
        // TODO context에서 Bean을 조회하여 각 서비스 구현체 할당 코드 작성하세요.
        // TODO: ??? 여기서 파라미터 주려면 어떻게 해야하는거지?
        userService = context.getBean(UserService.class);
        channelService = context.getBean(ChannelService.class);
        messageService = context.getBean(MessageService.class);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user, "Test!!!!!!!!");
    }

}
