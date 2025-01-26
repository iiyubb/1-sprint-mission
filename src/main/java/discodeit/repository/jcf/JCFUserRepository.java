package discodeit.repository.jcf;

import discodeit.entity.User;
import discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class JCFUserRepository implements UserRepository {
    private Map<String, User> userData;

    public JCFUserRepository() {
        this.userData = new HashMap<>();
    }

    @Override
    public void save(User user) {
        userData.put(user.getUserId(), user);
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
    }
}
