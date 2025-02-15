package discodeit.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class User {
    @JsonIgnore
    private UUID id;

    @JsonIgnore
    private Long createdAt;

    private String username;
    private String email;
    private String phoneNum;
    private String password;
    private Long updatedAt;

    // 생성자
    protected User() {
    }

    protected User(UUID id, Long createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public User(String username, String email, String phoneNum, String password) {
        this(UUID.randomUUID(), Instant.now().getEpochSecond());
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
            this.updatedAt = Instant.now().getEpochSecond();
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            this.updatedAt = Instant.now().getEpochSecond();
        }
        if (newPhoneNum != null && !newPhoneNum.equals(this.phoneNum)) {
            this.phoneNum = newPhoneNum;
            this.updatedAt = Instant.now().getEpochSecond();
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }

}
