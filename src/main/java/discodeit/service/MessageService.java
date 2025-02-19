package discodeit.service;

import discodeit.dto.binarycontent.AddBinaryContentRequest;
import discodeit.dto.message.CreateMessageRequest;
import discodeit.dto.message.MessageDto;
import discodeit.dto.message.UpdateMessageRequest;
import discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(CreateMessageRequest createMessageRequest, List<AddBinaryContentRequest> addBinaryContentRequests);
    MessageDto find(UUID messageId);
    List<MessageDto> findAllByChannelId(UUID channelId);
    Message update(UUID messageId, UpdateMessageRequest updateMessageRequest);
    void delete(UUID messageId);
}