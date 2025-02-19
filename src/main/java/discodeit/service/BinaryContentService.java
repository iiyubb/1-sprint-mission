package discodeit.service;

import discodeit.dto.binarycontent.CreateBinaryContentRequest;
import discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(CreateBinaryContentRequest createBinaryContentRequest);
    BinaryContent find(UUID binaryContentId);
    List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
}
