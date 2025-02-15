package discodeit.repository.jcf;

import discodeit.entity.User;
import discodeit.repository.UserRepository;
import discodeit.utils.FileUtil;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private Map<String, User> userData;

    public JCFUserRepository() {
        this.userData = new HashMap<>();
    }

    @Override
    public User save(User user) {
        userData.put(user.getId().toString(), user);
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
    }

}
