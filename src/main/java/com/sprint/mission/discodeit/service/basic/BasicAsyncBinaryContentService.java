package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.AsyncTaskFailure;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UploadStatus;
import com.sprint.mission.discodeit.repository.AsyncTaskFailureRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.AsyncBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.micrometer.core.annotation.Timed;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAsyncBinaryContentService implements AsyncBinaryContentService {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final AsyncTaskFailureRepository asyncTaskFailureRepository;

  @Async("taskExecutor")
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  @Timed(value = "async.file.upload", description = "비동기 파일 업로드 시간")
  public CompletableFuture<Void> uploadFileAsync(UUID binaryContentId, byte[] fileData) {
    String requestId = MDC.get("requestId");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return CompletableFuture.runAsync(() -> {
      try {
        MDC.put("requestId", requestId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("비동기 파일 업로드 시작: binaryContent ID = {}, request ID = {}", binaryContentId,
            requestId);
        Thread.sleep(2000);

        if (binaryContentId.toString().startsWith("ffffffff")) {
          throw new RuntimeException("재시도 메커니즘 테스트용 예외");
        }

        binaryContentStorage.put(binaryContentId, fileData);

        updateBinaryContentStatus(binaryContentId, UploadStatus.SUCCESS);
        log.info("비동기 파일 업로드 완료: binaryContent ID = {}, request ID = {}", binaryContentId,
            requestId);
      } catch (Exception e) {
        log.error("비동기 파일 업로드 중 오류 발생: binaryContent ID = {}, request ID = {}", binaryContentId,
            requestId);
        throw new RuntimeException("파일 업로드 실패", e);
      } finally {
        MDC.clear();
        SecurityContextHolder.clearContext();
      }
    });
  }

  @Recover
  public CompletableFuture<Void> recoverFromUploadFailure(Exception ex, UUID binaryContentId,
      byte[] fileData) {
    String requestId = MDC.get("requestId");

    log.error("모든 재시도 실패: binaryContent ID = {}, request ID = {}", binaryContentId, requestId);

    AsyncTaskFailure failure = new AsyncTaskFailure(
        requestId,
        "FILE_UPLOAD",
        binaryContentId,
        ex.getMessage(),
        3);

    asyncTaskFailureRepository.save(failure);
    updateBinaryContentStatus(binaryContentId, UploadStatus.FAILED);
    return CompletableFuture.completedFuture(null);
  }

  @Transactional
  public void updateBinaryContentStatus(UUID binaryContentId, UploadStatus status) {
    binaryContentRepository.findById(binaryContentId)
        .ifPresent(binaryContent -> {
          binaryContent.setUploadStatus(status);
          binaryContentRepository.save(binaryContent);
          log.info("바이너리 컨텐츠 상태 업데이트 완료: binaryContent ID = {}", binaryContent);
        });
  }


}
