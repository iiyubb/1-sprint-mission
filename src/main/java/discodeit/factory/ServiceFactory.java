package discodeit.factory;

import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;

public interface ServiceFactory {
    UserService createUserService();

    ChannelService createChannelService();

    MessageService createMessageService();
}
