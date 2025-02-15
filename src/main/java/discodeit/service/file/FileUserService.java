package discodeit.service.file;

import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.UserService;
import discodeit.utils.FileUtil;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;


public class FileUserService implements Serializable, UserService {
    private static final long serialVersionUID = 1L;
    private final Path directory;

    public FileUserService(Path directory) {
        this.directory = directory;
        FileUtil.init(directory);
    }

    @Override
    public User create(String userName, String email, String phoneNum, String password) {
        Map<String, User> userData = FileUtil.load(directory, User.class);

        if (isEmailDuplicate(userData, email)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
        }
        if (isValidEmail(email)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
        }
        if (isPhoneNumDuplicate(userData, phoneNum)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 전화번호입니다.");
        }
        if (isValidPhoneNum(phoneNum)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 전화번호 형식입니다.");
        }

        User newUser = new User(userName, email, phoneNum, password);
        userData.put(newUser.getId().toString(), newUser);
        FileUtil.save(directory, userData);
        return newUser;
    }

    @Override
    public User find(UUID userId) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        try {
            User user = userData.get(userId.toString());
            return user;
        } catch (Exception e) {
            throw new NoSuchElementException("[error] 존재하지 않는 User ID입니다.");
        }
    }

    @Override
    public List<User> findAll() {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        return userData.values().stream().toList();
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPhoneNum, String newPassword) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        if (!userData.containsKey(userId.toString())) {
            throw new NoSuchElementException("[error] 존재하지 않는 User ID입니다.");
        }
        if (isEmailDuplicate(userData, newEmail)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
        }
        if (isValidEmail(newEmail)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
        }
        if (isPhoneNumDuplicate(userData, newPhoneNum)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 전화번호입니다.");
        }
        if (isValidPhoneNum(newPhoneNum)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 전화번호 형식입니다. '010-0000-0000' 형식으로 작성해 주세요.");
        }
        User user = userData.get(userId.toString());

        user.update(newUsername, newEmail, newPhoneNum, newPassword);
        userData.put(userId.toString(), user);
        FileUtil.save(directory, userData);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        if (!userData.containsKey(userId.toString())) {
            throw new NoSuchElementException("[error] 존재하지 않는 User ID입니다.");
        }
        userData.remove(userId.toString());
        FileUtil.save(directory, userData);
        System.out.println("[삭제 완료]");
    }

    private boolean isEmailDuplicate(Map<String, User> userData, String email) {
        return userData.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }
    private boolean isValidEmail(String email) {
        String emailRegExp = "\\w+@\\w+\\.\\w+(\\.\\w+)?";
        return !email.matches(emailRegExp);
    }

    private boolean isPhoneNumDuplicate(Map<String, User> userData, String phoneNum) {
        return userData.values().stream().anyMatch(user -> user.getPhoneNum().equals(phoneNum));
    }
    private boolean isValidPhoneNum(String phoneNum) {
        String phoneNumRegExp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
        return !phoneNum.matches(phoneNumRegExp);
    }

}