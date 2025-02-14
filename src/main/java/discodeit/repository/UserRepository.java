package discodeit.repository;

import discodeit.entity.User;

import java.nio.file.Path;
import java.util.Map;

public interface UserRepository {
    void save(User user);
    User loadById(String userId);
    Map<String, User> loadAll();
    void delete(User user);
}