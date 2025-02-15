package discodeit.factory;

import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;

import discodeit.service.jcf.JCFChannelService;
import discodeit.service.jcf.JCFMessageService;
import discodeit.service.jcf.JCFUserService;

public class JCFServiceFactory implements ServiceFactory {

    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;

    public JCFServiceFactory() {
        this.userService = createUserService();
        this.channelService = createChannelService();
        this.messageService = createMessageService();
    }

    @Override
    public UserService createUserService() {
        if (userService == null) {
            userService = new JCFUserService();
        }
        return userService;
    }

    @Override
    public MessageService createMessageService() {
        if (messageService == null) {
            messageService = new JCFMessageService();
        }
        return messageService;
    }

    @Override
    public ChannelService createChannelService() {
        if (channelService == null) {
            channelService = new JCFChannelService(userService);
        }
        return channelService;
    }


}