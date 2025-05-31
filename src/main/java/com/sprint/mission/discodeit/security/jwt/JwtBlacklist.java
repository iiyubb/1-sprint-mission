package com.sprint.mission.discodeit.security.jwt;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class JwtBlacklist {

  private final ConcurrentHashMap<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public JwtBlacklist() {
    scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
  }

  public void addToBlacklist(String accessToken, Instant expirationTime) {
    blacklistedTokens.put(accessToken, expirationTime);
  }

  public boolean isBlacklisted(String accessToken) {
    Instant expirationTime = blacklistedTokens.get(accessToken);
    if (expirationTime == null) {
      return false;
    }

    if (Instant.now().isAfter(expirationTime)) {
      blacklistedTokens.remove(accessToken);
      return false;
    }
    return true;
  }

  private void cleanupExpiredTokens() {
    Instant now = Instant.now();
    blacklistedTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
  }

  public int getBlacklistSize() {
    return blacklistedTokens.size();
  }
}
