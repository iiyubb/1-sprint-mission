package discodeit;

import discodeit.dto.channel.CreatePublicChannelRequest;
import discodeit.dto.message.CreateMessageRequest;
import discodeit.dto.user.CreateUserRequest;
import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {
    static User setupUser(UserService userService) {
        CreateUserRequest request = new CreateUserRequest("woody", "woody@codeit.com", "010-1111-2222", "woody1234");
        User user = userService.create(request, Optional.empty());
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest("공지", "공지 채널입니다.", Optional.empty());
        Channel channel = channelService.create(request);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        CreateMessageRequest request = new CreateMessageRequest("안녕하세요.", channel.getId(), author.getId());
        Message message = messageService.create(request, new ArrayList<>());
        System.out.println("메시지 생성: " + message.getId());
    }

    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;


    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 서비스 초기화
        userService = context.getBean(UserService.class);
        channelService = context.getBean(ChannelService.class);
        messageService = context.getBean(MessageService.class);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);
    }

}
