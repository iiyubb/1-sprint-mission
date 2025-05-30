package com.sprint.mission.discodeit.security.jwt;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JwtRepository extends JpaRepository<JwtSession, UUID> {

  Optional<JwtSession> findByRefreshTokenAndIsActiveTrue(String refreshToken);

  Optional<JwtSession> findByAccessTokenAndIsActiveTrue(String accessToken);

  @Query("SELECT j FROM JwtSession j WHERE j.user.id = :userId AND j.isActive = true")
  Optional<JwtSession> findActiveSessionByUserId(@Param("userId") UUID userId);

  @Modifying
  @Query("UPDATE JwtSession j SET j.isActive = false WHERE j.user.id = :userId")
  void invalidateAllSessionByUserId(@Param("userId") UUID userId);

  @Modifying
  @Query("DELETE FROM JwtSession j WHERE j.isActive = false OR j.refreshTokenExpiresAt < CURRENT_TIMESTAMP")
  void deleteInactiveAndExpiredSessions();
}
