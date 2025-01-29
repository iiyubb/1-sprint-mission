package discodeit.service.file;

import discodeit.entity.User;
import discodeit.service.UserService;
import discodeit.utils.FileUtil;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;


public class FileUserService implements Serializable, UserService {
    private static final long serialVersionUID = 1L;
    private final Path directory;

    public FileUserService(Path directory) {
        this.directory = directory;
        FileUtil.init(directory);
    }

    @Override
    public void create(User newUser) {
        String userId = newUser.getUserId();
        String userName = newUser.getUserName();
        String phoneNum = newUser.getPhoneNum();
        String email = newUser.getEmail();

        // 예외처리
        if (isUserIdDuplicate(userId)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 user ID입니다.");
        }
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("[error] 유효하지 않은 user 이름입니다.");
        }
        if (isPhoneNumDuplicate(phoneNum)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 전화번호입니다.");
        }
        if (isValidPhoneNum(phoneNum)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 전화번호 형식입니다. '010-0000-0000' 형식으로 작성해 주세요.");
        }
        if (isValidEmail(email)) {
            throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
        }

        Map<String, User> userData = FileUtil.load(directory, User.class);
        userData.put(userId, newUser);
        FileUtil.save(directory, userData);
    }

    @Override
    public User readById(String userId) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        if (!userData.containsKey(userId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 user ID입니다.");
        }
        return userData.get(userId);
    }

    @Override
    public List<User> readAll() {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        return userData.values().stream().toList();
    }

    @Override
    public User update(String userId, User updateUser) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        if (!userData.containsKey(userId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 user ID입니다.");
        }
        User originUser = userData.get(userId);

        // 예외처리
        if (isPhoneNumDuplicate(updateUser.getPhoneNum())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 전화번호입니다.");
        }
        if (isValidPhoneNum(updateUser.getPhoneNum())) {
            throw new IllegalArgumentException("[error] 유효하지 않은 전화번호 형식입니다. '010-0000-0000' 형식으로 작성해 주세요.");
        }
        if (isEmailDuplicate((updateUser.getEmail()))) {
            throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
        }
        if (isValidEmail(updateUser.getEmail())) {
            throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
        }

        originUser.updatePhoneNum(updateUser.getPhoneNum());
        originUser.updateEmail(updateUser.getEmail());
        FileUtil.save(directory, userData);
        return originUser;
    }

    @Override
    public void delete(String userId) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        if (!userData.containsKey(userId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 user ID입니다.");
        }

        userData.remove(userId);
        FileUtil.save(directory, userData);
        System.out.println("[삭제 완료]");
    }

    // 확인
    private boolean isUserIdDuplicate(String userId) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        return userData.containsKey(userId);
    }

    private boolean isPhoneNumDuplicate(String phoneNum) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        return userData.values().stream().anyMatch(user -> user.getPhoneNum().equals(phoneNum));
    }

    private boolean isEmailDuplicate(String email) {
        Map<String, User> userData = FileUtil.load(directory, User.class);
        return userData.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isValidPhoneNum(String phoneNum) {
        String phoneNumRegExp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
        return !phoneNum.matches(phoneNumRegExp);
    }

    private boolean isValidEmail(String email) {
        String emailRegExp = "\\w+@\\w+\\.\\w+(\\.\\w+)?";
        return !email.matches(emailRegExp);
    }

}
