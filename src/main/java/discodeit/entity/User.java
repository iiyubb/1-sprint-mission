package discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class User {
    private UUID id;
    private Instant createdAt;

    private String username;
    private String email;
    private String phoneNum;
    private String password;
    private Instant updatedAt;

    protected User() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public User(String username, String email, String phoneNum, String password) {
        this();
        this.username = username;
        this.email = email;
        this.phoneNum = phoneNum;
        // TODO: 이렇게 되면 password가 외부에서 접근가능한 거 아닌지??
        this.password = password;
    }

    // Setter
    public void update(String newUsername, String newEmail, String newPhoneNum, String newPassword) {
        boolean anyValueUpdated = false;

        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            this.updatedAt = Instant.now();
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            this.updatedAt = Instant.now();
        }
        if (newPhoneNum != null && !newPhoneNum.equals(this.phoneNum)) {
            this.phoneNum = newPhoneNum;
            this.updatedAt = Instant.now();
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            this.updatedAt = Instant.now();
        }
    }

}
