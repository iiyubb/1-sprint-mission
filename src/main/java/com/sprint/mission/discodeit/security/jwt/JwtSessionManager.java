package com.sprint.mission.discodeit.security.jwt;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.aspectj.util.IStructureModel;
import org.springframework.stereotype.Component;

@Component
public class JwtSessionManager {

  private final ConcurrentHashMap<UUID, Set<String>> userSessions = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, SessionInfo> sessionInfoMap = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public JwtSessionManager() {
    scheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 30, 30, TimeUnit.MINUTES);
  }

  public void addSession(UUID userId, String sessionId, String accessToken,
      Instant expirationTime) {
    userSessions.compute(userId, (key, sessions) -> {
      if (sessions == null) {
        sessions = ConcurrentHashMap.newKeySet();
      }
      sessions.add(sessionId);
      return sessions;
    });

    sessionInfoMap.put(sessionId, new SessionInfo(userId, accessToken, expirationTime));
  }

  public void removeSession(String sessionId) {
    SessionInfo sessionInfo = sessionInfoMap.remove(sessionId);
    if (sessionInfo != null) {
      userSessions.computeIfPresent(sessionInfo.getUserId(), (userId, sessions) -> {
        sessions.remove(sessionId);
        return sessions.isEmpty() ? null : sessions;
      });
    }
  }

  public Set<String> removeAllUserSessions(UUID userId) {
    Set<String> sessions = userSessions.remove(userId);
    Set<String> accessTokens = new HashSet<>();

    if (sessions != null) {
      for (String sessionId : sessions) {
        SessionInfo info = sessionInfoMap.remove(sessionId);
        if (info != null) {
          accessTokens.add(info.getAccessToken());
        }
      }
    }
    return accessTokens;
  }

  public int getActiveSessionCount(UUID userId) {
    Set<String> sessions = userSessions.get(userId);
    return sessions != null ? sessions.size() : 0;
  }

  public boolean isUserLoggedIn(UUID userId) {
    return getActiveSessionCount(userId) > 0;
  }

  public String removeOldestSession(UUID userId) {
    Set<String> sessions = userSessions.get(userId);
    if (sessions == null || sessions.isEmpty()) {
      return null;
    }

    String oldestSessionId = sessions.stream()
        .min((s1, s2) -> {
          SessionInfo info1 = sessionInfoMap.get(s1);
          SessionInfo info2 = sessionInfoMap.get(s2);
          if (info1 == null || info2 == null) {
            return 0;
          }
          return info1.getCreatedAt().compareTo(info2.getCreatedAt());
        })
        .orElse(null);

    if (oldestSessionId != null) {
      SessionInfo sessionInfo = sessionInfoMap.get(oldestSessionId);
      removeSession(oldestSessionId);
      return sessionInfo != null ? sessionInfo.getAccessToken() : null;
    }
    return null;
  }

  private void cleanupExpiredSessions() {
    Instant now = Instant.now();
    sessionInfoMap.entrySet().removeIf(entry -> {
      SessionInfo info = entry.getValue();
      if (now.isAfter(info.getExpirationTime())) {
        userSessions.computeIfPresent(info.getUserId(), (userId, sessions) -> {
          sessions.remove(entry.getKey());
          return sessions.isEmpty() ? null : sessions;
        });
        return true;
      }
      return false;
    });
  }

  private static class SessionInfo {

    private final UUID userId;
    private final String accessToken;
    private final Instant expirationTime;
    private final Instant createdAt;

    public SessionInfo(UUID userId, String accessToken, Instant expirationTime) {
      this.userId = userId;
      this.accessToken = accessToken;
      this.expirationTime = expirationTime;
      this.createdAt = Instant.now();
    }

    public UUID getUserId() {
      return userId;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public Instant getExpirationTime() {
      return expirationTime;
    }

    public Instant getCreatedAt() {
      return createdAt;
    }

  }
}
