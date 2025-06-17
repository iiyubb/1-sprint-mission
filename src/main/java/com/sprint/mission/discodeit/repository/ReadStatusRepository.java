package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelID);

  List<ReadStatus> findByChannelIdAndNotificationEnabledTrue(UUID channelId);

  List<ReadStatus> findAllByUserId(UUID userId);

  @Query("SELECT r FROM ReadStatus r "
      + "JOIN FETCH r.user u "
      + "LEFT JOIN FETCH u.profile "
      + "WHERE r.channel.id = :channelId")
  List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);

  @Modifying
  @Query("UPDATE ReadStatus rs SET rs.notificationEnabled = :enabled WHERE rs.user.id = :userId AND rs.channel.id = :channelId")
  int updateNotificationEnabled(@Param("userId") UUID userId,
      @Param("channelId") UUID channelId,
      @Param("enabled") Boolean enabled);

  Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

  void deleteAllByChannelId(UUID channelId);

  UUID user(User user);
}
