package discodeit.dto.binarycontent;

import discodeit.entity.BinaryContentType;

public record AddBinaryContentRequest(String filename,
                                      BinaryContentType type,
                                      byte[] bytes) {
}
