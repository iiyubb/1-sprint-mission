package discodeit.repository                                                                                                                                                                                                                                                                                                                                                                                                                   ;

import discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}