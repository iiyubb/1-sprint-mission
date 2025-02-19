package discodeit.repository.file;

import discodeit.entity.ReadStatus;
import discodeit.entity.User;
import discodeit.repository.ReadStatusRepository;
import discodeit.utils.FileUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private Map<String, ReadStatus> readStatusData;
    private Path path;

    public FileReadStatusRepository(@Qualifier("readStatusFilPath") Path path) {
        this.path = path;
        if (!Files.exists(this.path)) {
            try {
                Files.createFile(this.path);
                FileUtil.save(this.path, new HashMap<>());
            } catch (IOException e) {
                throw new RuntimeException("[error] 메세지 읽은 시간 저장 파일을 초기화 불가능", e);
            }
        }
        FileUtil.init(this.path);
        this.readStatusData = FileUtil.load(this.path, ReadStatus.class);  // 변경된 메서드로 파일 데이터 로딩
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatusData.put(readStatus.getId().toString(), readStatus);
        FileUtil.save(path, readStatusData);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        if (!readStatusData.containsKey(id.toString())) {
            throw new NoSuchElementException("[error] 존재하지 않는 read status ID입니다.");
        }
        return Optional.ofNullable(readStatusData.get(id.toString()));
    }

    @Override
    public boolean existsById(UUID id) {
        return readStatusData.containsKey(id.toString());
    }

    @Override
    public void deleteById(UUID id) {
        if (!existsById(id)) {
            throw new NoSuchElementException("[error] 존재하지 않는 read status ID입니다.");
        }
        readStatusData.remove(id.toString());
        FileUtil.save(path, readStatusData);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        readStatusData.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
        FileUtil.save(path, readStatusData);
    }
}
