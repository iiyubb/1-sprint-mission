package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class AppConfig {

  @Bean
  public Path userFilePath() {
    return Path.of("src/main/java/com/sprint/mission/discodeit/data/User.json");
  }

  @Bean
  public FileUserRepository fileUserRepository(Path userFilePath) {
    return new FileUserRepository(userFilePath);
  }

  @Bean
  public Path channelFilePath() {
    return Path.of("src/main/java/com/sprint/mission/discodeit/data/Channel.json");
  }

  @Bean
  public FileChannelRepository fileChannelRepository(Path channelFilePath) {
    return new FileChannelRepository(channelFilePath);
  }

  @Bean
  public Path messageFilePath() {
    return Path.of("src/main/java/com/sprint/mission/discodeit/data/Message.json");
  }

  @Bean
  public FileMessageRepository fileMessageRepository(Path messageFilePath) {
    return new FileMessageRepository(messageFilePath);
  }

  @Bean
  public Path userStatusFilePath() {
    return Path.of("src/main/java/com/sprint/mission/discodeit/data/UserStatus.json");
  }

  @Bean
  public FileUserStatusRepository fileUserStatusRepository(Path userStatusFilePath) {
    return new FileUserStatusRepository(userStatusFilePath);
  }

  @Bean
  public Path readStatusFilPath() {
    return Path.of("src/main/java/com/sprint/mission/discodeit/data/ReadStatus.json");
  }

  @Bean
  public FileReadStatusRepository fileReadStatusRepository(Path readStatusFilPath) {
    return new FileReadStatusRepository(readStatusFilPath);
  }

  @Bean
  public Path binaryContentFilePath() {
    return Path.of("src/main/java/com/sprint/mission/discodeit/data/BinaryContent.json");
  }

  @Bean
  public FileBinaryContentRepository fileBinaryContentRepository(Path binaryContentFilePath) {
    return new FileBinaryContentRepository(binaryContentFilePath);
  }

}
