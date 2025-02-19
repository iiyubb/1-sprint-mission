package discodeit.service.basic;

import discodeit.dto.user.LoginRequest;
import discodeit.entity.User;
import discodeit.repository.UserRepository;
import discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new NoSuchElementException("[error] 해당 User name과 일치하는 User가 없습니다."));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new IllegalArgumentException("Wrong password");
        }
        return user;
    }
}
