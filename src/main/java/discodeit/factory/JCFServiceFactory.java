package discodeit.factory;

import discodeit.service.jcf.JCFChannelService;
import discodeit.service.jcf.JCFMessageService;
import discodeit.service.jcf.JCFUserService;

public class JCFServiceFactory implements ServiceFactory{

    private JCFUserService userService;
    private JCFChannelService channelService;
    private JCFMessageService messageService;

    public JCFServiceFactory() {
        this.userService = createUserService();
        this.channelService = createChannelService();
        this.messageService = createMessageService();
    }

    @Override
    public JCFUserService createUserService() {
        if (userService == null) {
            userService = new JCFUserService();
        }
        return userService;
    }

    @Override
    public JCFMessageService createMessageService() {
        if (messageService == null) {
            messageService = new JCFMessageService();
        }
        return messageService;
    }

    @Override
    public JCFChannelService createChannelService() {
        if (channelService == null) {
            channelService = new JCFChannelService(messageService);
        }
        return channelService;
    }


}
