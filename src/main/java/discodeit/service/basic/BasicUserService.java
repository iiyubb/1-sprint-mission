package discodeit.service.basic;

import discodeit.dto.binarycontent.AddBinaryContentRequest;
import discodeit.dto.user.CreateUserRequest;
import discodeit.dto.user.UpdateUserRequest;
import discodeit.dto.user.UserDto;
import discodeit.entity.BinaryContent;
import discodeit.entity.BinaryContentType;
import discodeit.entity.User;
import discodeit.entity.UserStatus;
import discodeit.repository.BinaryContentRepository;
import discodeit.repository.UserStatusRepository;
import discodeit.service.UserService;
import discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepo;
    private final UserStatusRepository userStatusRepo;
    private final BinaryContentRepository binaryContentRepo;

    @Override
    public User create(CreateUserRequest createUserRequest, Optional<AddBinaryContentRequest> addBinaryContentRequest) {
        if (isUsernameDuplicate(createUserRequest.username())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 사용자 이름입니다.");
        }
        if (isEmailDuplicate(createUserRequest.email())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
        }
        if (isValidEmail(createUserRequest.email())) {
            throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
        }
        if (isPhoneNumDuplicate(createUserRequest.phoneNum())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 전화번호입니다.");
        }
        if (isValidPhoneNum(createUserRequest.phoneNum())) {
            throw new IllegalArgumentException("[error] 유효하지 않은 전화번호 형식입니다.");
        }

        UUID nullableProfileId = addBinaryContentRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.filename();
                    BinaryContentType contentType = profileRequest.type();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, (long)bytes.length, bytes);
                    return binaryContentRepo.save(binaryContent).getId();
                })
                .orElse(null);

        // user 객체를 생성할 때도 createUserRequest DTO를 사용하면 안되나요?
        // User newUser = new User(CreateUserRequset createUserRequset); 이런 식으로요
        User newUser = new User(createUserRequest.username(), createUserRequest.email(), createUserRequest.phoneNum(), createUserRequest.password(), nullableProfileId);
        return userRepo.save(newUser);
    }

    @Override
    public UserDto find(UUID userId) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
        return toDto(findUser);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public User update(UUID userId, UpdateUserRequest updateUserRequest, Optional<AddBinaryContentRequest> addBinaryContentRequest) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));

        if (isUsernameDuplicate(updateUserRequest.newUsername())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 사용자 이름입니다.");
        }
        if (isEmailDuplicate((updateUserRequest.newEmail()))) {
            throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
        }
        if (isValidEmail(updateUserRequest.newEmail())) {
            throw new IllegalArgumentException("[error] 유효하지 않은 E-mail 형식입니다.");
        }
        if (isPhoneNumDuplicate(updateUserRequest.newPhoneNum())) {
            throw new IllegalArgumentException("[error] 이미 존재하는 전화번호입니다.");
        }
        if (isValidPhoneNum(updateUserRequest.newPhoneNum())) {
            throw new IllegalArgumentException("[error] 유효하지 않은 전화번호 형식입니다. '010-0000-0000' 형식으로 작성해 주세요.");
        }

        UUID nullableProfileId = addBinaryContentRequest
                .map(profileRequest -> {
                    Optional.ofNullable(user.getProfileId())
                            .ifPresent(binaryContentRepo::deleteById);

                    String fileName = profileRequest.filename();
                    BinaryContentType contentType = profileRequest.type();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, (long) bytes.length, bytes);
                    return binaryContentRepo.save(binaryContent).getId();
                })
                .orElse(null);

        String newPassword = updateUserRequest.newPassword();
        user.update(updateUserRequest.newUsername(), updateUserRequest.newEmail(), updateUserRequest.newPhoneNum(), updateUserRequest.newPassword(), nullableProfileId);

        return userRepo.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));

        Optional.ofNullable(user.getProfileId())
                .ifPresent(binaryContentRepo::deleteById);
        userStatusRepo.deleteByUserId(userId);

        userRepo.deleteById(userId);
        System.out.println("[삭제 완료]");
    }

    private UserDto toDto(User user) {
        Boolean online = userStatusRepo.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNum(),
                user.getUpdatedAt(),
                online);
    }

    private boolean isUsernameDuplicate(String username) {
        return userRepo.findAll().stream().anyMatch(user -> user.getUsername().equals(username));
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