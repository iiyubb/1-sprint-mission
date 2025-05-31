package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtService;
import com.sprint.mission.discodeit.security.jwt.JwtSessionManager;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class BasicAuthService implements AuthService {

  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtSessionManager jwtSessionManager;
  private final int maxConcurrentSessions;

  @Value("${ADMIN_USERNAME}")
  private String username;
  @Value("${ADMIN_EMAIL}")
  private String email;
  @Value("${ADMIN_PASSWORD}")
  private String password;

  public BasicAuthService(JwtService jwtService, UserRepository userRepository,
      UserMapper userMapper,
      PasswordEncoder passwordEncoder,
      JwtSessionManager jwtSessionManager,
      @Value("${security.max-concurrent-sessions:1}") int maxConcurrentSessions) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.jwtSessionManager = jwtSessionManager;
    this.maxConcurrentSessions = maxConcurrentSessions;
  }

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

  @Override
  public TokenResponse login(UUID userId) {
    // 동시 로그인 제한 확인
    if (jwtSessionManager.getActiveSessionCount(userId) >= maxConcurrentSessions) {
      // 가장 오래된 세션의 액세스 토큰을 가져와서 무효화
      String oldAccessToken = jwtSessionManager.removeOldestSession(userId);
      if (oldAccessToken != null) {
        String oldSessionId = jwtService.getSessionIdFromToken(oldAccessToken);
        jwtService.invalidateSession(oldSessionId, oldAccessToken);
      }
    }

    // 새 세션 생성
    String sessionId = UUID.randomUUID().toString();
    String accessToken = jwtService.generateAccessToken(userId, sessionId);
    String refreshToken = jwtService.generateRefreshToken(userId);

    return new TokenResponse(accessToken, refreshToken);
  }

  @Override
  public void logout(String accessToken) {
    if (jwtService.isValidAccessToken(accessToken)) {
      String sessionId = jwtService.getSessionIdFromToken(accessToken);
      jwtService.invalidateSession(sessionId, accessToken);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(UserRoleUpdateRequest request) {
    log.info("사용자 권한 업데이트 요청: userId = {}, newRole = {}",
        request.userId(), request.newRole());

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + request.userId()));

    // 기존 권한과 동일한지 확인
    if (user.getRole().equals(request.newRole())) {
      log.info("사용자의 권한이 이미 {}입니다", request.newRole());
      return userMapper.toDto(user);
    }

    // 권한 업데이트
    user.updateRole(request.newRole());
    User updatedUser = userRepository.save(user);

    // 권한 변경 시 모든 세션 무효화 (보안상 중요)
    jwtService.invalidateAllUserSessions(updatedUser.getId());

    log.info("사용자 권한 업데이트 완료: userId = {}, role = {}, 모든 세션 무효화됨",
        updatedUser.getId(), updatedUser.getRole());

    return userMapper.toDto(updatedUser);
  }

  @Override
  public boolean isUserLoggedIn(UUID userId) {
    return jwtSessionManager.isUserLoggedIn(userId);
  }

  @Override
  public TokenResponse refreshToken(String refreshToken) {
    String newAccessToken = jwtService.refreshAccessToken(refreshToken);
    return new TokenResponse(newAccessToken, refreshToken);
  }

  @Override
  public int getActiveSessionCount(UUID userId) {
    return jwtSessionManager.getActiveSessionCount(userId);
  }

  public static class TokenResponse {

    private final String accessToken;
    private final String refreshToken;

    public TokenResponse(String accessToken, String refreshToken) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public String getRefreshToken() {
      return refreshToken;
    }
  }
}