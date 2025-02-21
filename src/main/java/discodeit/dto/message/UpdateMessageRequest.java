package discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record UpdateMessageRequest(String newMessageDetail,
                                   UUID writerId,
                                   List<UUID> fileIdToDelete) {
}
