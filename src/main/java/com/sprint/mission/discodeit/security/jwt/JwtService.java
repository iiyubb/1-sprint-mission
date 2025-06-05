package com.sprint.mission.discodeit.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService {

  private final SecretKey secretKey;
  private final long accessTokenValidityInMs;
  private final long refreshTokenValidityInMs;
  private final JwtBlacklist jwtBlacklist;
  private final JwtSessionManager jwtSessionManager;

  public JwtService(@Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-validity}") long accessTokenValidityInMs,
      @Value("${jwt.refresh-token-validity}") long refreshTokenValidityInMs,
      JwtBlacklist jwtBlacklist,
      JwtSessionManager jwtSessionManager) {

    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.accessTokenValidityInMs = accessTokenValidityInMs;
    this.refreshTokenValidityInMs = refreshTokenValidityInMs;
    this.jwtBlacklist = jwtBlacklist;
    this.jwtSessionManager = jwtSessionManager;
  }

  public String generateAccessToken(UUID userId, String sessionId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + accessTokenValidityInMs);

    String accessToken = Jwts.builder()
        .setSubject(userId.toString())
        .claim("sessionId", sessionId)
        .claim("type", "access")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(secretKey)
        .compact();

    Instant expirationTime = expiryDate.toInstant()
        .atZone(ZoneId.systemDefault())
        .toInstant();
    jwtSessionManager.addSession(userId, sessionId, accessToken, expirationTime);

    return accessToken;
  }

  public String generateRefreshToken(UUID userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenValidityInMs);

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("type", "refresh")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(secretKey)
        .compact();
  }

  public UUID getUserIdFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return UUID.fromString(claims.getSubject());
  }

  public String getSessionIdFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return (String) claims.get("sessionId");
  }

  public Instant getExpirationFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return claims.getExpiration().toInstant()
        .atZone(ZoneId.systemDefault())
        .toInstant();
  }

  private Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      throw new IllegalArgumentException("Token expired", e);
    } catch (UnsupportedJwtException e) {
      throw new IllegalArgumentException("Unsupported token", e);
    } catch (MalformedJwtException e) {
      throw new IllegalArgumentException("Malformed token", e);
    } catch (JwtException e) {
      throw new IllegalArgumentException("Invalid token signature", e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid token", e);
    }
  }

  public boolean isValidAccessToken(String token) {
    try {
      if (jwtBlacklist.isBlacklisted(token)) {
        return false;
      }

      Claims claims = getClaimsFromToken(token);

      // 토큰 타입 확인
      String tokenType = (String) claims.get("type");
      if (!"access".equals(tokenType)) {
        return false;
      }

      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean isValidRefreshToken(String token) {
    try {
      Claims claims = getClaimsFromToken(token);

      String tokenType = (String) claims.get("type");
      return "refresh".equals(tokenType);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public void invalidateAllUserSessions(UUID userId) {
    Set<String> accessTokens = jwtSessionManager.removeAllUserSessions(userId);

    for (String accessToken : accessTokens) {
      try {
        Instant expirationTime = getExpirationFromToken(accessToken);
        jwtBlacklist.addToBlacklist(accessToken, expirationTime);
      } catch (Exception e) {

      }
    }
  }

  public void invalidateSession(String sessionId, String accessToken) {
    try {
      Instant expirationTime = getExpirationFromToken(accessToken);
      jwtBlacklist.addToBlacklist(accessToken, expirationTime);
    } catch (Exception e) {
    }

    jwtSessionManager.removeSession(sessionId);
  }

  public String refreshAccessToken(String refreshToken) {
    if (!isValidRefreshToken(refreshToken)) {
      throw new IllegalArgumentException("Invalid refresh token");
    }

    UUID userId = getUserIdFromToken(refreshToken);
    String sessionId = java.util.UUID.randomUUID().toString();

    return generateAccessToken(userId, sessionId);
  }

}
