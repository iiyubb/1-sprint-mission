package com.sprint.mission.discodeit.security.jwt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JwtSessionRepository extends JpaRepository<JwtSession, UUID> {

  List<JwtSession> findByUserId(UUID userId);

  Optional<JwtSession> findByAccessToken(String accessToken);

  Optional<JwtSession> findByRefreshToken(String refreshToken);

  boolean existsByAccessToken(String accessToken);

  boolean existsByRefreshToken(String refreshToken);

  List<JwtSession> findByUserIdAndExpirationTimeAfter(UUID userId, Instant currentTime);

  int countByUserIdAndExpirationTimeAfter(UUID userId, Instant currentTime);

  @Modifying
  int deleteByAccessToken(String accessToken);

  @Modifying
  int deleteByRefreshToken(String refreshToken);

  @Modifying
  int deleteByUserId(UUID userId);

  @Modifying
  @Query("DELETE FROM JwtSession s WHERE s.expirationTime < :currentTime")
  int deleteExpiredSession(@Param("currentTime") Instant currentTime);

  @Query("SELECT s FROM JwtSession s WHERE s.userId = :userId ORDER BY s.createdAt ASC LIMIT 1")
  Optional<JwtSession> findOldestSessionByUserId(@Param("userId") UUID userId);

  @Query("SELECT s.accessToken FROM JwtSession s WHERE s.userId = :userId")
  List<String> findAccessTokensByUserId(@Param("userId") UUID userId);
  
}
