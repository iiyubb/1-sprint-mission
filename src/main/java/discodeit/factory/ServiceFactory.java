package discodeit.factory;

import discodeit.service.jcf.JCFChannelService;
import discodeit.service.jcf.JCFMessageService;
import discodeit.service.jcf.JCFUserService;

public interface ServiceFactory {
    JCFUserService createUserService();
    JCFChannelService createChannelService();
    JCFMessageService createMessageService();
}
