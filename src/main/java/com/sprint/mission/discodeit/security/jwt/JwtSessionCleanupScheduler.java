package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtSessionCleanupScheduler {

  private final JwtService jwtService;

  @Scheduled(fixedRate = 3600000)
  public void cleanupExpiredSessions() {
    try {
      jwtService.cleanupExpiredSessions();
      ;
      log.debug("Completed cleanup of expired JWT sessions");
    } catch (Exception e) {
      log.error("Error during JWT session cleanup", e);
    }
  }
}
