package discodeit.service;

import discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String phoneNum, String password);
    User find(UUID userId);
    List<User> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPhoneNum, String newPassword);
    void delete(UUID userId);
}
