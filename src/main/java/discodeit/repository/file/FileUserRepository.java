package discodeit.repository.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import discodeit.entity.User;
import discodeit.repository.UserRepository;
import discodeit.utils.FileUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {
    private Map<String, User> userData;
    private Path path;
    private ObjectMapper objectMapper;

    public FileUserRepository(@Qualifier("userFilePath") Path path) {
        this.path = path;

        // ObjectMapper 초기화 및 JavaTimeModule 등록
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());  // JavaTimeModule을 등록하여 Instant 처리

        if (!Files.exists(this.path)) {
            try {
                Files.createFile(this.path);
                FileUtil.save(this.path, new HashMap<>());
            } catch (IOException e) {
                throw new RuntimeException("[error] User 파일을 초기화 불가능", e);
            }
        }
        FileUtil.init(this.path);
        this.userData = FileUtil.load(this.path, User.class);  // 변경된 메서드로 파일 데이터 로딩
    }

    @Override
    public User save(User user) {
        userData.put(user.getId().toString(), user);
        FileUtil.save(path, userData);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        if (!userData.containsKey(userId.toString())) {
            throw new IllegalArgumentException("[error] 존재하지 않는 user ID입니다.");
        }
        return Optional.ofNullable(userData.get(userId.toString()));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userData.values().stream()
                .filter(user -> user.getUsername().equals(username)).findAny();
    }

    @Override
    public List<User> findAll() {
        return userData != null ? userData.values().stream().toList() : new ArrayList<>();
    }

    @Override
    public boolean existsById(UUID userId) {
        return userData.containsKey(userId.toString());
    }

    @Override
    public void deleteById(UUID userId) {
        userData.remove(userId.toString());
        FileUtil.save(path, userData);
    }

}
