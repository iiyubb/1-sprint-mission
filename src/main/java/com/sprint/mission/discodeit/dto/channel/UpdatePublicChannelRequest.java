package com.sprint.mission.discodeit.dto.channel;

import java.util.Optional;
import java.util.UUID;

// only PUBLIC channel
public record UpdateChannelRequest(String newName,
                                   String newDescription) {

}
