package discodeit.repository.file;

import discodeit.entity.UserStatus;
import discodeit.repository.UserStatusRepository;
import discodeit.utils.FileUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private Map<String, UserStatus> userStatusData;
    private Path path;

    public FileUserStatusRepository(@Qualifier("userStatusFilePath") Path path) {
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
        this.userStatusData = FileUtil.load(this.path, UserStatus.class);  // 변경된 메서드로 파일 데이터 로딩
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatusData.put(userStatus.getId().toString(), userStatus);
        FileUtil.save(path, userStatusData);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(userStatusData.get(id.toString()));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        return List.of();
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteByUserId(UUID userId) {

    }
}
