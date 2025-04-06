package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(GlobalExceptionHandler.class)
public class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ChannelService channelService;

  @MockitoBean
  private AuthController authController;

  @MockitoBean
  private BinaryContentController binaryContentController;

  @MockitoBean
  private MessageController messageController;

  @MockitoBean
  private ReadStatusController readStatusController;

  @MockitoBean
  private UserController userController;

  @Test
  @DisplayName("POST /channels - 성공")
  void createPrivateChannelSuccess() throws Exception {
    UUID channelId = UUID.randomUUID();
    UserDto user1 = new UserDto(UUID.randomUUID(), "yubin", "yubin@codeit.com", null, true);
    UserDto user2 = new UserDto(UUID.randomUUID(), "minji", "minji@codeit.com", null, true);

    when(channelService.create(any(PrivateChannelCreateRequest.class)))
        .thenReturn(
            new ChannelDto(channelId, ChannelType.PRIVATE, null, null, List.of(user1, user2),
                Instant.now()));

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.format("""
                {
                  "participantIds" : ["%s", "%s"]
                }""", user1.id().toString(), user2.id().toString())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()));
  }

}
