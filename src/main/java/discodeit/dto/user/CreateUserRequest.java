package discodeit.dto.user;

public record CreateUserRequest(String username,
                                String email,
                                String phoneNum,
                                String password) {
}
