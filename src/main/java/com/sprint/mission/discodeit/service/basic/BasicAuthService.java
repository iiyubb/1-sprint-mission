package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.security.InvalidTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.CustomUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtService;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final SessionRegistry sessionRegistry;
  private final JwtService jwtService;

  @Value("${ADMIN_USERNAME}")
  private String username;
  @Value("${ADMIN_EMAIL}")
  private String email;
  @Value("${ADMIN_PASSWORD}")
  private String password;

  @Override
  public UserDto initAdmin() {
    if (userRepository.existsByEmail(email) || userRepository.existsByUsername(username)) {
      log.warn("이미 존재하는 관리자입니다.");
      return null;
    }

    String encodedPw = passwordEncoder.encode(password);
    User admin = new User(username, email, encodedPw, null);
    admin.updateRole(Role.ADMIN);
    userRepository.save(admin);

    UserDto adminDto = userMapper.toDto(admin);
    log.info("관리자가 초기화되었습니다. ID = {}", adminDto.id());
    return adminDto;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(UserRoleUpdateRequest request) {
    UUID userId = request.userId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Role oldRole = user.getRole();
    user.updateRole(request.newRole());

    if (!oldRole.equals(request.newRole())) {
      log.info("User role changed from {} to {} for user {}, forcing logout", oldRole,
          request.newRole(), user.getUsername());
    }

    sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> ((CustomUserDetails) principal).getUserDto().id().equals(userId))
        .findFirst()
        .ifPresent(principal -> {
          List<SessionInformation> activeSessions = sessionRegistry.getAllSessions(principal,
              false);
          log.debug("활성화 세션: {}", activeSessions.size());
          activeSessions.forEach(SessionInformation::expireNow);
        });

    jwtService.invalidateAllUserSessions(userId);
    log.info("All JWT sessions invalidated for user: {}", user.getUsername());

    return userMapper.toDto(user);
  }
}