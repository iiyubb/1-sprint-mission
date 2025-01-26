package discodeit.factory;

import discodeit.repository.ChannelRepository;
import discodeit.repository.MessageRepository;
import discodeit.repository.UserRepository;
import discodeit.repository.jcf.JCFChannelRepository;
import discodeit.repository.jcf.JCFMessageRepository;
import discodeit.repository.jcf.JCFUserRepository;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.service.basic.BasicChannelService;
import discodeit.service.basic.BasicMessageService;
import discodeit.service.basic.BasicUserService;


public class BasicServiceFactory implements ServiceFactory {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;

    public BasicServiceFactory() {
        this.userRepository = new JCFUserRepository();
        this.messageRepository = new JCFMessageRepository();
        this.channelRepository = new JCFChannelRepository();

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