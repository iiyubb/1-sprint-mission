package discodeit.factory;

import discodeit.repository.ChannelRepository;
import discodeit.repository.MessageRepository;
import discodeit.repository.UserRepository;
import discodeit.repository.file.FileChannelRepository;
import discodeit.repository.file.FileMessageRepository;
import discodeit.repository.file.FileUserRepository;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.service.basic.BasicChannelService;
import discodeit.service.basic.BasicMessageService;
import discodeit.service.basic.BasicUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class BasicServiceFactory implements ServiceFactory {

    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;

    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;

    private BasicServiceFactory() {
    }

    public BasicServiceFactory(@Qualifier("userFilePath") Path userPath,
                               @Qualifier("channelFilePath") Path channelPath,
                               @Qualifier("messageFilePath") Path messagePath) {
        this.userRepository = new FileUserRepository(userPath);
        this.messageRepository = new FileMessageRepository(channelPath);
        this.channelRepository = new FileChannelRepository(messagePath);

        this.userService = new BasicUserService(userRepository);
        this.messageService = new BasicMessageService(messageRepository);
        this.channelService = new BasicChannelService(channelRepository, userRepository);
    }

    @Override
    public UserService createUserService() {
        if (userService == null) {
            userService = new BasicUserService(userRepository);
        }
        return userService;
    }

    @Override
    public MessageService createMessageService() {
        if (messageService == null) {
            messageService = new BasicMessageService(messageRepository);
        }
        return messageService;
    }

    @Override
    public ChannelService createChannelService() {
        if (channelService == null) {
            channelService = new BasicChannelService(channelRepository, userRepository);
        }
        return channelService;
    }
}