package discodeit.service;

import discodeit.dto.channel.ChannelDto;
import discodeit.dto.channel.CreatePrivateChannelRequest;
import discodeit.dto.channel.CreatePublicChannelRequest;
import discodeit.dto.channel.UpdateChannelRequest;
import discodeit.entity.Channel;
import discodeit.entity.ChannelType;
import discodeit.entity.Message;
import discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(CreatePublicChannelRequest createPublicChannelRequest);
    Channel create(CreatePrivateChannelRequest createPrivateChannelRequest);
    ChannelDto find(UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);
    Channel update(UUID channelId, UpdateChannelRequest updateChannelRequest);
    void delete(UUID channelId);
    void addParticipant(UUID channelId, UUID userId);
    void deleteParticipant(UUID channelId, UUID userId);
}
