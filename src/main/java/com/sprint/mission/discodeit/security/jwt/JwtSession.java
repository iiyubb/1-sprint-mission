package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "jwt_sessions")
@Getter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtSession extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "access_token", columnDefinition = "TEXT", nullable = false)
  private String accessToken;

  @Column(name = "refresh_token", columnDefinition = "TEXT", nullable = false)
  private String refreshToken;

  @Column(name = "access_token_expires_at", nullable = false)
  private Instant accessTokenExpiresAt;

  @Column(name = "refresh_token_expires_at", nullable = false)
  private Instant refreshTokenExpiresAt;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  public JwtSession(User user, String accessToken, String refreshToken,
      Instant accessTokenExpiresAt, Instant refreshTokenExpiresAt) {
    this.user = user;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.accessTokenExpiresAt = accessTokenExpiresAt;
    this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    this.isActive = true;
  }

  public void updateTokens(String newAccessToken, String newRefreshToken,
      Instant accessTokenExpiresAt, Instant refreshTokenExpiresAt) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
    this.accessTokenExpiresAt = accessTokenExpiresAt;
    this.refreshTokenExpiresAt = refreshTokenExpiresAt;
  }

  public void updateAccessToken(String newAccessToken, Instant newAccessTokenExpiryAt) {
    this.accessToken = newAccessToken;
    this.accessTokenExpiresAt = newAccessTokenExpiryAt;
  }

  public void invalidate() {
    this.isActive = false;
  }

  public boolean isRefreshTokenExpired() {
    return Instant.now().isAfter(refreshTokenExpiresAt);
  }

  public boolean isAccessTokenExpired() {
    return Instant.now().isAfter(accessTokenExpiresAt);
  }
}