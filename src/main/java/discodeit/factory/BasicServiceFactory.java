package discodeit.factory;

import discodeit.repository.ChannelRepository;
import discodeit.repository.MessageRepository;
import discodeit.repository.UserRepository;
import discodeit.repository.file.FileChannelRepository;
import discodeit.repository.file.FileMessageRepository;
import discodeit.repository.file.FileUserRepository;
import discodeit.repository.jcf.JCFChannelRepository;
import discodeit.repository.jcf.JCFMessageRepository;
import discodeit.repository.jcf.JCFUserRepository;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.service.basic.BasicChannelService;
import discodeit.service.basic.BasicMessageService;
import discodeit.service.basic.BasicUserService;

import java.nio.file.Path;


public class BasicServiceFactory implements ServiceFactory {

    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;

    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;

    public BasicServiceFactory() {

    }

    public BasicServiceFactory(Path userPath, Path channelPath, Path messagePath) {
        this.userRepository = new FileUserRepository(userPath);
        this.messageRepository = new FileMessageRepository(messagePath);
        this.channelRepository = new FileChannelRepository(channelPath);

        this.userService = new BasicUserService(userRepository);
        this.messageService = new BasicMessageService(messageRepository);
        this.channelService = new BasicChannelService(channelRepository, messageService);
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
            channelService = new BasicChannelService(channelRepository, messageService);
        }
        return channelService;
    }
}