package discodeit.service.basic;

import discodeit.entity.User;
import discodeit.service.UserService;
import discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepo;

    @Override
    public User create(String userName, String email, String phoneNum, String password) {
        if (isEmailDuplicate(email)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
        }
        if (isValidEmail(email)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
        }
        if (isPhoneNumDuplicate(phoneNum)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 전화번호입니다.");
        }
        if (isValidPhoneNum(phoneNum)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 전화번호 형식입니다.");
        }

        User newUser = new User(userName, email, phoneNum, password);
        return userRepo.save(newUser);
    }

    @Override
    public User find(UUID userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPhoneNum, String newPassword) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
        if (isEmailDuplicate((newEmail))) {
            throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
        }
        if (isValidEmail(newEmail)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
        }
        if (isPhoneNumDuplicate(newPhoneNum)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 전화번호입니다.");
        }
        if (isValidPhoneNum(newPhoneNum)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 전화번호 형식입니다. '010-0000-0000' 형식으로 작성해 주세요.");
        }

        user.update(newUsername, newEmail, newPhoneNum, newPassword);
        return userRepo.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepo.existsById(userId)) {
            throw new NoSuchElementException("[error] 존재하지 않는 User ID입니다.");
        }
        userRepo.deleteById(userId);
        System.out.println("[삭제 완료]");
    }

    private boolean isEmailDuplicate(String email) {
        return userRepo.findAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }
    private boolean isValidEmail(String email) {
        String emailRegExp = "\\w+@\\w+\\.\\w+(\\.\\w+)?";
        return !email.matches(emailRegExp);
    }

    private boolean isPhoneNumDuplicate(String phoneNum) {
        return userRepo.findAll().stream().anyMatch(user -> user.getPhoneNum().equals(phoneNum));
    }
    private boolean isValidPhoneNum(String phoneNum) {
        String phoneNumRegExp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
        return !phoneNum.matches(phoneNumRegExp);
    }

}