package discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private UUID profileId;
    private Instant updatedAt;

    protected User() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public User(String username, String email, String phoneNum, String password, UUID profileId) {
        this();
        this.username = username;
        this.email = email;
        this.phoneNum = phoneNum;
        this.password = password;
        this.profileId = profileId;
    }

    // Setter
    public void update(String newUsername, String newEmail, String newPhoneNum, String newPassword, UUID newProfileId) {
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
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            this.updatedAt = Instant.now();
        }
    }

}
