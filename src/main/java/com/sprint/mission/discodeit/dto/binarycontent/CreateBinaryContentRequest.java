package discodeit.dto.binarycontent;

import discodeit.entity.BinaryContentType;

public record CreateBinaryContentRequest(String filename,
                                         BinaryContentType type,
                                         byte[] bytes) {
}
