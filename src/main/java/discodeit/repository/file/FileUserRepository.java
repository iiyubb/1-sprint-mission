package discodeit.repository.file;

import discodeit.entity.User;
import discodeit.repository.UserRepository;
import discodeit.utils.FileUtil;

import java.nio.file.*;
import java.util.Map;


public class FileUserRepository implements UserRepository {
    private Map<String, User> userData;
    private Path path;

    public FileUserRepository() {
    }

    public FileUserRepository(Path path) {
        this.path = path;
        FileUtil.init(path);
        this.userData = FileUtil.load(path, User.class);
    }


    @Override
    public void save(User user) {
        userData.put(user.getUserId(), user);
        FileUtil.save(path, userData);
    }

    @Override
    public User loadById(String userId) {
        if (!userData.containsKey(userId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 user ID입니다.");
        }
        return userData.get(userId);
    }

    @Override
    public Map<String, User> loadAll() {
        return userData;
    }

    @Override
    public void delete(User user) {
        userData.remove(user.getUserId());
        FileUtil.save(path, userData);
    }

}
