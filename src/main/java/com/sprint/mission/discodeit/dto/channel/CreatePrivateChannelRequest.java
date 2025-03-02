package discodeit.dto.channel;

import java.util.UUID;

public record CreatePrivateChannelRequest(
                                          UUID participant1,
                                          UUID participant2) {
}
