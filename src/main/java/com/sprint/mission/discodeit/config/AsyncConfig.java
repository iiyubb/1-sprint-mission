package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

  @Bean(name = "taskExecutor")
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("async-file-upload-");
    executor.setRejectedExecutionHandler(new CallerRunsPolicy());

    executor.setTaskDecorator(new ContextAwareTaskDecorator());
    executor.initialize();
    return executor;
  }

  public static class ContextAwareTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      return () -> {
        try {
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }
          if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
          runnable.run();
        } finally {
          MDC.clear();
          SecurityContextHolder.clearContext();
        }
      };
    }
  }
}
