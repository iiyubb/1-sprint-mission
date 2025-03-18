package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannelRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto create(CreatePublicChannelRequest request);

  ChannelDto create(CreatePrivateChannelRequest request);

  ChannelDto findById(UUID channelId);

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelDto update(UUID channelId, UpdatePublicChannelRequest updateChannelRequest);

  void delete(UUID channelId);
}
