package discodeit.service;

import discodeit.dto.user.LoginRequest;
import discodeit.entity.User;

public interface AuthService {
    User login(LoginRequest loginRequest);
}
