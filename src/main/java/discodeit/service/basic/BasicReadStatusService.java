package discodeit.service.basic;

import discodeit.dto.readstatus.CreateReadStatusRequest;
import discodeit.dto.readstatus.UpdateReadStatusRequest;
import discodeit.entity.ReadStatus;
import discodeit.repository.ChannelRepository;
import discodeit.repository.ReadStatusRepository;
import discodeit.repository.UserRepository;
import discodeit.service.ReadStatusService;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicReadStatusService implements ReadStatusService {
    private ReadStatusRepository readStatusRepo;
    private UserRepository userRepo;
    private ChannelRepository channelRepo;

    @Override
    public ReadStatus create(CreateReadStatusRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        if (!userRepo.existsById(userId)) { throw new NoSuchElementException("[error] 존재하지 않는 User ID입니다."); }
        if (!channelRepo.existsById(channelId)) { throw new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다."); }
        if (readStatusRepo.findAllByUserId(userId).stream().anyMatch(readStatus -> readStatus.getChannelId().equals(channelId))) {
            throw new IllegalArgumentException("[error] 이미 존재하는 Read Status입니다.");
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(userId, channelId, lastReadAt);
        return readStatusRepo.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepo.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 Read Status ID입니다."));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepo.findAllByUserId(userId).stream().toList();
    }

    @Override
    public ReadStatus update(UUID readStatusId, UpdateReadStatusRequest request) {
        ReadStatus readStatus = readStatusRepo.findById(readStatusId).orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 Read Status ID입니다."));
        readStatus.update(request.newLastReadAt());
        return readStatusRepo.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepo.existsById(readStatusId)) { throw new NoSuchElementException("[error] 존재하지 않는 Read Status ID입니다."); }
        readStatusRepo.deleteById(readStatusId);
    }
}
