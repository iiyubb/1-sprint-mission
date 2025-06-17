package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.UploadStatus;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AsyncBinaryContentService {

  CompletableFuture<Void> uploadFileAsync(UUID binaryContentId, byte[] fileData);

  CompletableFuture<Void> recoverFromUploadFailure(Exception ex, UUID binaryContentId,
      byte[] fileData);

  void updateBinaryContentStatus(UUID binaryContentId, UploadStatus status);
}
