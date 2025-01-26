package discodeit.service.basic;

import discodeit.entity.User;
import discodeit.service.UserService;
import discodeit.repository.UserRepository;

import java.util.List;
import java.util.Map;

public class BasicUserService implements UserService {
    private UserRepository userRepo;

    public BasicUserService() {

    }

    public BasicUserService(UserRepository userRepo) {
        this.userRepo = userRepo;
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

        userRepo.save(newUser);
    }

    @Override
    public User readById(String userId) {
        if (!userRepo.loadAll().containsKey(userId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 유저 ID입니다.");
        }
        return userRepo.loadById(userId);
    }

    @Override
    public List<User> readAll() {
        return userRepo.loadAll().values().stream().toList();
    }

    @Override
    public User update(String userId, User updateUser) {
        Map<String, User> userData = userRepo.loadAll();

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
        userRepo.save(originUser);
        return originUser;
    }

    @Override
    public void delete(String userId) {
        userRepo.delete(userRepo.loadById(userId));
        System.out.println("[삭제 완료]");
    }

    // 확인
    // 이렇게 매번 예외처리를 할때마다 loadAll로 repository를 읽어오는 게 맞는지,
    // 생성자에서 loadAll을 통해 읽어서 userData map에 저장하는 게 나을지 고민
    private boolean isUserIdDuplicate(String userId) {
        return userRepo.loadAll().containsKey(userId);
    }

    private boolean isPhoneNumDuplicate(String phoneNum) {
        return userRepo.loadAll().values().stream().anyMatch(user -> user.getPhoneNum().equals(phoneNum));
    }

    private boolean isEmailDuplicate(String email) {
        return userRepo.loadAll().values().stream().anyMatch(user -> user.getEmail().equals(email));
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
