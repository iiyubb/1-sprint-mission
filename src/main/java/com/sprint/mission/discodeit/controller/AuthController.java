package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.exception.security.ExpiredTokenException;
import com.sprint.mission.discodeit.exception.security.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.security.InvalidTokenException;
import com.sprint.mission.discodeit.security.CustomUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtService;
import com.sprint.mission.discodeit.security.jwt.JwtTokenPair;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest requset,
      HttpServletResponse response) {
    log.info("로그인 요청: 사용자 = {}", requset.username());

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(requset.username(), requset.password())
      );

      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

      JwtTokenPair tokenPair = jwtService.generateTokens(userDetails.getUserDto());

      ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenPair.refreshToken())
          .httpOnly(true)
          .secure(true)
          .path("/")
          .maxAge(7 * 24 * 60 * 60)
          .sameSite("Strict")
          .build();

      response.addHeader("Set-Cookie", refreshCookie.toString());

      log.info("로그인 성공: 사용자 ={}", requset.username());
      return ResponseEntity.ok(tokenPair.accessToken());
    } catch (AuthenticationException e) {
      log.warn("로그인 실패: 사용자 ={}, 사유 = {}", requset.username(), e.getMessage());

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of(
              "error", "AUTHENTICATION_FAILED",
              "message", "사용자 이름 또는 비밀번호가 올바르지 않습니다."
          ));
    }
  }

  @GetMapping("/csrf-token")
  public ResponseEntity<CsrfToken> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(csrfToken);
  }

  @GetMapping("/me")
  public ResponseEntity<String> getAccessToken(HttpServletRequest request) {
    try {
      String refreshToken = extractRefreshTokenFromCookie(request);

      if (refreshToken == null) {
        log.debug("No refresh token found in cookies");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      String accessToken = jwtService.getAccessTokenByRefreshToken(refreshToken);

      if (accessToken == null) {
        log.debug("Failed to get access token for refresh token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      return ResponseEntity.ok(accessToken);

    } catch (Exception e) {
      log.error("Error getting access token", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PutMapping("/role")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> roleUpdate(@Valid @RequestBody UserRoleUpdateRequest request) {
    log.info("사용자 권한 수정 요청: 사용자 ID = {}", request.userId());
    UserDto updateUserDto = authService.updateRole(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updateUserDto);
  }

  @PostMapping("/refresh")
  public ResponseEntity<String> refreshToken(
      @CookieValue(value = "refresh_token", required = false) String refreshToken,
      HttpServletResponse response) {

    if (refreshToken == null || refreshToken.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Refresh token not found");
    }

    try {
      JwtTokenPair tokenPair = jwtService.refreshTokens(refreshToken);

      // 새로운 리프레시 토큰을 쿠키에 저장
      Cookie refreshCookie = new Cookie("refresh_token", tokenPair.refreshToken());
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(true); // HTTPS 환경에서만 사용
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
      response.addCookie(refreshCookie);

      // 액세스 토큰을 문자열로 반환
      return ResponseEntity.ok(tokenPair.accessToken());

    } catch (InvalidRefreshTokenException | ExpiredTokenException e) {
      log.debug("Token refresh failed: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error during token refresh", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Token refresh failed");
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
    try {
      String refreshToken = extractRefreshTokenFromCookie(request);

      if (refreshToken != null) {
        jwtService.invalidateRefreshToken(refreshToken);
        log.info("Refresh token invalidated successfully");
      }

      invalidateRefreshTokenCookie(response);

      return ResponseEntity.ok().build();

    } catch (Exception e) {
      log.error("Error during logout", e);

      invalidateRefreshTokenCookie(response);
      return ResponseEntity.ok().build();
    }
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> "refresh_token".equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }

  private void invalidateRefreshTokenCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("refresh_token", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    response.addCookie(cookie);
  }
}
