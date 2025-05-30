package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.security.ExpiredTokenException;
import com.sprint.mission.discodeit.exception.security.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.security.InvalidTokenException;
import com.sprint.mission.discodeit.exception.security.TokenExtractionException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

  private final JwtProperties jwtProperties;
  private final JwtRepository jwtRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
  }

  @Transactional
  public JwtTokenPair generateTokens(UserDto userDto) {
    try {
      User user = userRepository.findById(userDto.id())
          .orElseThrow(() -> new UserNotFoundException().withId(userDto.id()));

      jwtRepository.invalidateAllSessionByUserId(user.getId());

      String userDtoJson = objectMapper.writeValueAsString(userDto);

      Instant now = Instant.now();
      Instant accessTokenExpiry = now.plus(jwtProperties.getAccessTokenValidityInMinutes(),
          ChronoUnit.MINUTES);
      Instant refreshTokenExpiry = now.plus(jwtProperties.getRefreshTokenValidityInDays(),
          ChronoUnit.DAYS);

      String accessToken = createToken(userDtoJson, accessTokenExpiry, TokenType.ACCESS);
      String refreshToken = createToken(userDtoJson, refreshTokenExpiry, TokenType.REFRESH);

      JwtSession jwtSession = new JwtSession(
          user,
          accessToken,
          refreshToken,
          accessTokenExpiry,
          refreshTokenExpiry
      );

      jwtRepository.save(jwtSession);
      return new JwtTokenPair(accessToken, refreshToken);
    } catch (JsonProcessingException e) {
      throw TokenExtractionException.withCause(e);
    }
  }

  private String createToken(String userDtoJson, Instant expiry, TokenType tokenType) {
    Date expiryDate = Date.from(expiry);
    Date issuedAt = Date.from(Instant.now());

    return Jwts.builder()
        .claim("userDto", userDtoJson)
        .claim("tokenType", tokenType.name())
        .issuedAt(issuedAt)
        .expiration(expiryDate)
        .signWith(getSigningKey(), SIG.HS256)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.debug("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }

  public Instant getTokenExpiration(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();

      return claims.getExpiration().toInstant();
    } catch (ExpiredJwtException e) {
      throw ExpiredTokenException.withTokenType("access");
    } catch (JwtException e) {
      throw InvalidTokenException.withMessage("Failed to extract expiration from token");
    } catch (Exception e) {
      throw TokenExtractionException.withField("expiration");
    }
  }

  public Instant getTokenIssuedAt(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();

      return claims.getIssuedAt().toInstant();
    } catch (ExpiredJwtException e) {
      throw ExpiredTokenException.withTokenType("access");
    } catch (JwtException e) {
      throw InvalidTokenException.withMessage("Failed to extract issued at from token");
    } catch (Exception e) {
      throw TokenExtractionException.withField("issuedAt");
    }
  }

  @Transactional
  public JwtTokenPair refreshTokens(String refreshToken) {
    JwtSession session = jwtRepository.findByRefreshTokenAndIsActiveTrue(refreshToken)
        .orElseThrow(() -> InvalidRefreshTokenException.notFound());

    if (session.isRefreshTokenExpired()) {
      session.invalidate();
      jwtRepository.save(session);
      throw ExpiredTokenException.withTokenType("refresh");
    }

    if (!validateToken(refreshToken)) {
      session.invalidate();
      jwtRepository.save(session);
      throw InvalidRefreshTokenException.withUserId(session.getUser().getId());
    }

    UserDto userDto = extractUserDto(refreshToken);

    try {
      String userDtoJson = objectMapper.writeValueAsString(userDto);

      Instant now = Instant.now();
      Instant accessTokenExpiry = now.plus(jwtProperties.getAccessTokenValidityInMinutes(),
          ChronoUnit.MINUTES);
      Instant refreshTokenExpiry = now.plus(jwtProperties.getRefreshTokenValidityInDays(),
          ChronoUnit.DAYS);

      String newAccessToken = createToken(userDtoJson, accessTokenExpiry, TokenType.ACCESS);
      String newRefreshToken = createToken(userDtoJson, refreshTokenExpiry, TokenType.REFRESH);

      session.updateTokens(newAccessToken, newRefreshToken, accessTokenExpiry, refreshTokenExpiry);
      jwtRepository.save(session);

      return new JwtTokenPair(newAccessToken, newRefreshToken);
    } catch (JsonProcessingException e) {
      throw TokenExtractionException.withCause(e);
    }
  }

  public String getAccessTokenByRefreshToken(String refreshToken) {
    try {
      if (!validateToken(refreshToken)) {
        log.debug("Invalid refresh token");
        return null;
      }

      Optional<JwtSession> sessionOpt = jwtRepository.findByRefreshTokenAndIsActiveTrue(
          refreshToken);

      if (sessionOpt.isEmpty()) {
        log.debug("No active session found for refresh token");
        return null;
      }

      JwtSession session = sessionOpt.get();

      if (session.isRefreshTokenExpired()) {
        log.debug("Refresh token expired, invalidation session");
        session.invalidate();
        jwtRepository.save(session);
        return null;
      }

      String accessToken = session.getAccessToken();

      if (session.isAccessTokenExpired()) {
        log.debug("Access token expired, renewing...");
        accessToken = renewAccessTokenOnly(session);
      }
      return accessToken;

    } catch (Exception e) {
      log.error("Error getting access token by refresh token", e);
      return null;
    }
  }

  private String renewAccessTokenOnly(JwtSession session) {
    try {
      UserDto userDto = extractUserDto(session.getRefreshToken());
      String userDtoJson = objectMapper.writeValueAsString(userDto);

      Instant now = Instant.now();
      Instant accessTokenExpiry = now.plus(jwtProperties.getAccessTokenValidityInMinutes(),
          ChronoUnit.MINUTES);

      String newAccessToken = createToken(userDtoJson, accessTokenExpiry, TokenType.ACCESS);

      session.updateAccessToken(newAccessToken, accessTokenExpiry);
      jwtRepository.save(session);

      log.debug("Access token renewed for user: {}", userDto.id());
      return newAccessToken;

    } catch (Exception e) {
      log.error("Error renewing access token", e);
      throw new RuntimeException("Failed to renew access token", e);
    }
  }

  @Transactional
  public void invalidateRefreshToken(String refreshToken) {
    Optional<JwtSession> sessionOpt = jwtRepository.findByRefreshTokenAndIsActiveTrue(refreshToken);
    if (sessionOpt.isPresent()) {
      JwtSession session = sessionOpt.get();
      session.invalidate();
      jwtRepository.save(session);
    }
  }

  @Transactional
  public void invalidateAllUserSessions(UUID userId) {
    jwtRepository.invalidateAllSessionByUserId(userId);
  }

  @Transactional
  public void cleanupExpiredSessions() {
    jwtRepository.deleteInactiveAndExpiredSessions();
  }

  public UserDto extractUserDto(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();

      String userDtoJson = claims.get("userDto", String.class);
      return objectMapper.readValue(userDtoJson, UserDto.class);

    } catch (JsonProcessingException e) {
      throw TokenExtractionException.withField("userDto");
    } catch (ExpiredJwtException e) {
      throw ExpiredTokenException.withTokenType("access");
    } catch (JwtException e) {
      throw InvalidTokenException.withCause(e);
    } catch (Exception e) {
      throw TokenExtractionException.withCause(e);
    }
  }

  private enum TokenType {
    ACCESS,
    REFRESH
  }
}
