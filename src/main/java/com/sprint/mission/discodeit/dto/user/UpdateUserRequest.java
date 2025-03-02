package discodeit.dto.user;

public record UpdateUserRequest(String newUsername,
                                String newEmail,
                                String newPhoneNum,
                                String newPassword) {
}
