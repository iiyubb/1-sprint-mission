package com.sprint.mission.discodeit.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import java.util.Date;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class JwtService {

  // TODO: 커스텀 예외 처리
  private final SecretKey secretKey;
  private final long accessTokenValidityInMs;
  private final long refreshTokenValidityInMs;
  private final JwtBlacklist jwtBlacklist;
  private final JwtSessionRepository sessionRepository;

  public JwtService(@Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-validity}") long accessTokenValidityInMs,
      @Value("${jwt.refresh-token-validity}") long refreshTokenValidityInMs,
      JwtBlacklist jwtBlacklist,
      JwtSessionRepository sessionRepository) {

    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.accessTokenValidityInMs = accessTokenValidityInMs;
    this.refreshTokenValidityInMs = refreshTokenValidityInMs;
    this.jwtBlacklist = jwtBlacklist;
    this.sessionRepository = sessionRepository;
  }

  @Transactional
  public TokenPair generateTokenPair(UUID userId) {
    try {
      Instant now = Instant.now();
      Instant accessExpiryTime = now.plusMillis(accessTokenValidityInMs);
      Instant refreshExpiryTime = now.plusMillis(refreshTokenValidityInMs);

      JwtSession session = new JwtSession(userId, "", "", refreshExpiryTime);
      UUID sessionId = session.getId();

      String accessToken = Jwts.builder()
          .subject(userId.toString())
          .claim("sessionId", sessionId.toString())
          .claim("type", "access")
          .issuedAt(Date.from(now))
          .expiration(Date.from(accessExpiryTime))
          .signWith(secretKey)
          .compact();

      String refreshToken = Jwts.builder()
          .subject(userId.toString())
          .claim("sessionId", sessionId.toString())
          .claim("type", "refresh")
          .issuedAt(Date.from(now))
          .expiration(Date.from(refreshExpiryTime))
          .signWith(secretKey)
          .compact();

      session.setAccessToken(accessToken);
      session.setRefreshToken(refreshToken);
      JwtSession updatedSession = sessionRepository.save(session);

      log.info("토큰 쌍 생성 완료 - 사용자 ID: {}, 세션 ID: {}", userId, sessionId);
      return new TokenPair(accessToken, refreshToken, updatedSession.getId());

    } catch (Exception e) {
      log.error("토큰 쌍 생성 실패 - 사용자: {}", userId, e);
      throw new RuntimeException("토큰 생성에 실패했습니다.", e);
    }
  }

  @Transactional
  public TokenPair refreshTokenPair(String refreshToken) {
    if (!isValidRefreshToken(refreshToken)) {
      log.warn("유효하지 않은 리프레시 토큰으로 갱신 시도");
      throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
    }

    try {
      UUID userId = getUserIdFromToken(refreshToken);
      removeSessionByRefreshToken(refreshToken);

      TokenPair newTokenPair = generateTokenPair(userId);

      log.info("토큰 갱신 완료 - 사용자 ID: {}, 새 세션 ID: {}", userId, newTokenPair.getSessionId());
      return newTokenPair;

    } catch (Exception e) {
      log.error("토큰 갱신 실패", e);
      throw new RuntimeException("토큰 갱신에 실패했습니다.", e);
    }
  }

  @Transactional
  public UUID getUserIdFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return UUID.fromString(claims.getSubject());
  }

  public UUID getSessionIdFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    String sessionIdStr = (String) claims.get("sessionId");
    return UUID.fromString(sessionIdStr);
  }

  public Instant getExpirationFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return claims.getExpiration().toInstant();
  }

  public boolean isValidAccessToken(String token) {
    try {
      if (jwtBlacklist.isBlacklisted(token)) {
        log.debug("블랙리스트에 등록된 토큰");
        return false;
      }
      Claims claims = getClaimsFromToken(token);

      String tokenType = (String) claims.get("type");
      if (!"access".equals(tokenType)) {
        log.debug("액세스 토큰이 아님");
        return false;
      }

      if (!sessionRepository.existsByAccessToken(token)) {
        log.debug("세션에 존재하지 않는 토큰");
        return false;
      }

      return true;
    } catch (Exception e) {
      log.debug("토큰 검증 실패: {}", e.getMessage());
      return false;
    }
  }

  public boolean isValidRefreshToken(String token) {
    try {
      Claims claims = getClaimsFromToken(token);

      String tokenType = (String) claims.get("type");
      if (!"refresh".equals(tokenType)) {
        log.debug("리프레시 토큰이 아님");
        return false;
      }
      if (!sessionRepository.existsByRefreshToken(token)) {
        log.debug("세션에 존재하지 않는 리프레시 토큰");
        return false;
      }
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.debug("리프레시 토큰 검증 실패: {}", e.getMessage());
      return false;
    }
  }

  private Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      log.debug("토큰이 만료됨: {}", e.getMessage());
      throw new IllegalArgumentException("토큰이 만료되었습니다", e);
    } catch (UnsupportedJwtException e) {
      log.debug("지원하지 않는 토큰: {}", e.getMessage());
      throw new IllegalArgumentException("지원하지 않는 토큰입니다", e);
    } catch (MalformedJwtException e) {
      log.debug("잘못된 형식의 토큰: {}", e.getMessage());
      throw new IllegalArgumentException("잘못된 형식의 토큰입니다", e);
    } catch (JwtException e) {
      log.debug("유효하지 않은 토큰 서명: {}", e.getMessage());
      throw new IllegalArgumentException("유효하지 않은 토큰 서명입니다", e);
    } catch (IllegalArgumentException e) {
      log.debug("유효하지 않은 토큰: {}", e.getMessage());
      throw new IllegalArgumentException("유효하지 않은 토큰입니다", e);
    }
  }

  @Transactional
  public void logout(String accessToken) {
    log.info("로그아웃 시작");

    try {
      Instant expirationTime = getExpirationFromToken(accessToken);
      jwtBlacklist.addToBlacklist(accessToken, expirationTime);
      log.debug("토큰을 블랙리스트에 추가");
    } catch (Exception e) {
      log.warn("토큰 블랙리스트에 추가 실패: {}", e.getMessage());
    }

    removeSessionByAccessToken(accessToken);
    log.info("로그아웃 완료");
  }

  @Transactional
  public void logoutAll(UUID userId) {
    log.info("전체 로그아웃 시작 - 사용자: {}", userId);

    List<String> accessTokens = sessionRepository.findAccessTokensByUserId(userId);
    for (String accessToken : accessTokens) {
      try {
        Instant expirationTime = getExpirationFromToken(accessToken);
        jwtBlacklist.addToBlacklist(accessToken, expirationTime);
      } catch (Exception e) {
        log.warn("토큰 블랙리스트에 추가 실패: {}", e.getMessage());
      }
    }

    int deletedCount = sessionRepository.deleteByUserId(userId);
    log.info("전체 로그아웃 완료 - 사용자 ID: {}, 제거된 세션 수: {}", userId, deletedCount);
  }

  @Transactional
  public void removeSessionByAccessToken(String accessToken) {
    try {
      int deletedCount = sessionRepository.deleteByAccessToken(accessToken);
      if (deletedCount > 0) {
        log.debug("액세스 토큰으로 세션 제거 완료");
      } else {
        log.warn("액세스 토큰으로 제거할 세션을 찾을 수 없음");
      }
    } catch (Exception e) {
      log.error("액세스 토큰으로 세션 제거 실패");
      throw new RuntimeException("세션 제거에 실패했습니다.", e);
    }
  }

  @Transactional
  public void removeSessionByRefreshToken(String refreshToken) {
    try {
      int deletedCount = sessionRepository.deleteByRefreshToken(refreshToken);
      if (deletedCount > 0) {
        log.debug("리프레시 토큰으로 세션 제거 완료");
      } else {
        log.warn("리프레시 토큰으로 제거할 세션을 찾을 수 없음");
      }
    } catch (Exception e) {
      log.error("리프레시 토근으로 세션 제거 실패", e);
      throw new RuntimeException("세션 제거에 실패했습니다.", e);
    }
  }

  @Transactional
  public boolean removeSession(UUID sessionId) {
    try {
      sessionRepository.deleteById(sessionId);
      log.debug("세션 제거 완료 - 세션 ID: ", sessionId);
      return true;
    } catch (Exception e) {
      log.error("세션 제거 실패 - 세션 ID: ", sessionId);
      return false;
    }
  }

  @Transactional
  public Set<String> removeAllUserSessions(UUID userId) {
    try {
      List<String> accessTokens = sessionRepository.findAccessTokensByUserId(userId);
      int deletedCount = sessionRepository.deleteByUserId(userId);

      log.info("사용자의 모든 세션 제거 완료 - 사용자 ID: {}, 제거된 세션 수: {}", userId, deletedCount);
      return new HashSet<>(accessTokens);

    } catch (Exception e) {
      log.error("사용자의 모든 세션 제거 실패 - 사용자 ID: {}", userId, e);
      throw new RuntimeException("사용자 세션 제거에 실패했습니다.", e);
    }
  }

  @Transactional(readOnly = true)
  public int getActiveSessionCount(UUID userId) {
    try {
      return sessionRepository.countByUserIdAndExpirationTimeAfter(userId, Instant.now());
    } catch (Exception e) {
      log.error("활성 세션 수 조회 실패 - 사용자 ID: {}", userId, e);
      return 0;
    }
  }

  @Transactional(readOnly = true)
  public boolean isUserLoggedIn(UUID userId) {
    return getActiveSessionCount(userId) > 0;
  }

  @Transactional
  public String removeOldestSession(UUID userId) {
    try {
      Optional<JwtSession> oldestSessionOpt = sessionRepository.findOldestSessionByUserId(userId);

      if (oldestSessionOpt.isEmpty()) {
        log.debug("제거할 세션이 없음 - 사용자 ID: {}", userId);
        return null;
      }

      JwtSession oldestSession = oldestSessionOpt.get();
      String accessToken = oldestSession.getAccessToken();
      sessionRepository.deleteByAccessToken(accessToken);

      log.debug("가장 오래된 세션 제거 완료 - 사용자 ID: {}", userId);
      return accessToken;

    } catch (Exception e) {
      log.error("가장 오래된 세션 제거 실패 - 사용자 ID: {}", userId, e);
      throw new RuntimeException("세션 제거에 실패했습니다.", e);
    }
  }

  @Transactional(readOnly = true)
  public Optional<JwtSession> getSession(UUID sessionId) {
    try {
      return sessionRepository.findById(sessionId);
    } catch (Exception e) {
      log.error("세션 정보 조회 실패 - 세션 ID: {}", sessionId);
      return Optional.empty();
    }
  }

  @Transactional(readOnly = true)
  public Optional<JwtSession> getSessionByAccessToken(String accessToken) {
    try {
      return sessionRepository.findByAccessToken(accessToken);
    } catch (Exception e) {
      log.error("액세스 토큰으로 세션 정보 조회 실패", e);
      return Optional.empty();
    }
  }

  @Transactional(readOnly = true)
  public Optional<JwtSession> getSessionByRefreshToken(String refreshToken) {
    try {
      return sessionRepository.findByRefreshToken(refreshToken);
    } catch (Exception e) {
      log.error("리프레시 토큰으로 세션 정보 조회 실패", e);
      return Optional.empty();
    }
  }

  @Transactional(readOnly = true)
  public List<JwtSession> getActiveSessions(UUID userId) {
    try {
      return sessionRepository.findByUserIdAndExpirationTimeAfter(userId, Instant.now());
    } catch (Exception e) {
      log.error("활성 세션 목록 조회 실패 - 사용자 ID: {}", userId);
      return List.of();
    }
  }

  @Transactional(readOnly = true)
  public List<JwtSession> getAllUserSessions(UUID userId) {
    try {
      return sessionRepository.findByUserId(userId);
    } catch (Exception e) {
      log.error("전체 세션 목록 조회 실패 - 사용자: {}", userId, e);
      return List.of();
    }
  }

  @Transactional(readOnly = true)
  public boolean existsByAccessToken(String accessToken) {
    try {
      return sessionRepository.existsByAccessToken(accessToken);
    } catch (Exception e) {
      log.error("액세스 토큰으로 세션 존재 확인 실패, e");
      return false;
    }
  }

  @Transactional(readOnly = true)
  public boolean existsByRefreshToken(String refreshToken) {
    try {
      return sessionRepository.existsByRefreshToken(refreshToken);
    } catch (Exception e) {
      log.error("리프레시 토큰으로 세션 존재 확인 실패", e);
      return false;
    }
  }

  @Transactional
  public void updateSession(UUID sessionId, String newAccessToken, String newRefreshToken,
      Instant newExpirationTime) {
    try {
      Optional<JwtSession> sessionOpt = sessionRepository.findById(sessionId);

      if (sessionOpt.isPresent()) {
        JwtSession session = sessionOpt.get();
        sessionRepository.deleteById(sessionId);

        JwtSession newSession = new JwtSession(
            session.getUserId(),
            newAccessToken,
            newRefreshToken,
            newExpirationTime
        );
        sessionRepository.save(newSession);

        log.debug("세션 갱신 완료 - 기존 세션 ID: {}, 새 세션 ID: {}", sessionId, newSession.getId());
      } else {
        log.warn("갱신할 세션을 찾을 수 없음 - 세션 ID: {}", sessionId);
      }
    } catch (Exception e) {
      log.error("세션 갱신 실패 - 세션 ID: {}", sessionId, e);
      throw new RuntimeException("세션 갱신에 실패했습니다.", e);
    }
  }

  @Scheduled(fixedRate = 30 * 60 * 1000)
  @Transactional
  public void cleanupExpiredSessions() {
    try {
      Instant now = Instant.now();
      int deletedCount = sessionRepository.deleteExpiredSession(now);

      if (deletedCount > 0) {
        log.info("만료된 세션 정리 완료 - 삭제된 세션 수: {}", deletedCount);
      } else {
        log.debug("정리할 만료된 세션이 없음");
      }
    } catch (Exception e) {
      log.error("만료된 세션 정리 실패", e);
    }
  }

  @Transactional(readOnly = true)
  public boolean isSessionExpired(UUID sessionId) {
    try {
      Optional<JwtSession> sessionOpt = sessionRepository.findById(sessionId);
      return sessionOpt.map(JwtSession::isExpired).orElse(true);
    } catch (Exception e) {
      log.error("세션 만료 확인 실패 - 세션 ID: {}", sessionId, e);
      return true;
    }
  }

  public static class TokenPair {

    @Getter
    private final String accessToken;
    @Getter
    private final String refreshToken;
    @Getter
    private final UUID sessionId;

    public TokenPair(String accessToken, String refreshToken, UUID sessionId) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.sessionId = sessionId;
    }

    @Override
    public String toString() {
      return "TokenPair{" +
          "sessionId=" + sessionId +
          '}';
    }
  }
}
