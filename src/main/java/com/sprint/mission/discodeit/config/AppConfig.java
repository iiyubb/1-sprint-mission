package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class AppConfig {

  @Bean
  public FileUserRepository fileUserRepository(
      @Value("${repository.user-file-path}") String path) {
    return new FileUserRepository(Path.of(path));
  }

  @Bean
  public FileChannelRepository fileChannelRepository(
      @Value("${repository.channel-file-path}") String path) {
    return new FileChannelRepository(Path.of(path));
  }

  @Bean
  public FileMessageRepository fileMessageRepository(
      @Value("${repository.message-file-path}") String path) {
    return new FileMessageRepository(Path.of(path));
  }

  @Bean
  public FileUserStatusRepository fileUserStatusRepository(
      @Value("${repository.user-status-file-path}") String path) {
    return new FileUserStatusRepository(Path.of(path));
  }

  @Bean
  public FileReadStatusRepository fileReadStatusRepository(
      @Value("${repository.read-status-file-path}") String path) {
    return new FileReadStatusRepository(Path.of(path));
  }

  @Bean
  public FileBinaryContentRepository fileBinaryContentRepository(
      @Value("${repository.binary-content-file-path}") String path) {
    return new FileBinaryContentRepository(Path.of(path));
  }

}
