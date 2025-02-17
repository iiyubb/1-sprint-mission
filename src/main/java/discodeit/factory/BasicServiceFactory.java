package discodeit.factory;

import discodeit.repository.*;
import discodeit.repository.file.FileChannelRepository;
import discodeit.repository.file.FileMessageRepository;
import discodeit.repository.file.FileUserRepository;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.service.basic.BasicChannelService;
import discodeit.service.basic.BasicMessageService;
import discodeit.service.basic.BasicUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class BasicServiceFactory implements ServiceFactory {

    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;
    private ReadStatusRepository readStatusRepository;
    private UserStatusRepository userStatusRepository;
    private BinaryContentRepository binaryContentRepository;

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

        this.userService = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository);
        this.messageService = new BasicMessageService(messageRepository, channelRepository, binaryContentRepository);
        this.channelService = new BasicChannelService(userRepository, channelRepository, messageRepository, readStatusRepository);
    }

    @Override
    public UserService createUserService() {
        if (userService == null) {
            userService = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository);
        }
        return userService;
    }

    @Override
    public MessageService createMessageService() {
        if (messageService == null) {
            messageService = new BasicMessageService(messageRepository, channelRepository, binaryContentRepository);
        }
        return messageService;
    }

    @Override
    public ChannelService createChannelService() {
        if (channelService == null) {
            channelService = new BasicChannelService(userRepository, channelRepository, messageRepository, readStatusRepository);
        }
        return channelService;
    }
}