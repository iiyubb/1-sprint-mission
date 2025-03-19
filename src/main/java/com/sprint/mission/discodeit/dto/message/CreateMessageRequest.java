package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public record CreateMessageRequest(String content,
                                   Channel channel,
                                   User author) {

}
