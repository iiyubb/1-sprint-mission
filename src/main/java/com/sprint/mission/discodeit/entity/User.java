package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
  private UserStatus userStatus;

  public void update(String newUsername, String newEmail, String newPassword,
      BinaryContent newProfile) {
    if (newUsername == null) {
      throw new IllegalArgumentException("[error] 사용자 이름이 입력되지 않았습니다.");
    }
    if (newUsername.equals(this.username)) {
      throw new IllegalArgumentException("[error] 현재 사용자 이름과 동일합니다.");
    }
    this.username = newUsername;

    if (newEmail == null) {
      throw new IllegalArgumentException("[error] 이메일이 입력되지 않았습니다.");
    }
    if (newEmail.equals(this.email)) {
      throw new IllegalArgumentException("[error] 현재 이메일과 동일합니다.");
    }
    this.email = newEmail;

    if (newPassword == null) {
      throw new IllegalArgumentException("[error] 비밀번호가 입력되지 않았습니다.");
    }
    if (newPassword.equals(this.password)) {
      throw new IllegalArgumentException("[error] 현재 비밀번호와 동일합니다.");
    }
    this.password = newPassword;

    if (profile.getId() != null && newProfile.getId() == null) {
      System.out.println("사용자 프로필 사진이 삭제되었습니다.");
    }
    if ((newProfile.getId() != null) && newProfile.getId().equals(this.profile.getId())) {
      throw new IllegalArgumentException("[error] 현재 프로필 ID와 동일합니다.");
    } else {
      this.profile = newProfile;
    }
  }
}
