package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.CustomUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtService;
import com.sprint.mission.discodeit.service.AuthService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
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
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
      HttpServletResponse response) {
    log.info("로그인 요청: 사용자 = {}", request.username());

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.username(), request.password())
      );

      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      UserDto userDto = userDetails.getUserDto();

      JwtService.TokenPair tokenPair = jwtService.generateTokenPair(userDto.id());

      ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",
              tokenPair.getRefreshToken())
          .httpOnly(true)
          .secure(true)
          .path("/")
          .maxAge(7 * 24 * 60 * 60)
          .sameSite("Strict")
          .build();

      response.addHeader("Set-Cookie", refreshCookie.toString());

      log.info("로그인 성공: 사용자 = {}", request.username());
      return ResponseEntity.ok(Map.of(
          "accessToken", tokenPair.getAccessToken(),
          "sessionId", tokenPair.getSessionId().toString()
      ));

    } catch (AuthenticationException e) {
      log.warn("로그인 실패: 사용자 ={}, 사유 = {}", request.username(), e.getMessage());

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
  public ResponseEntity<?> getAccessToken(HttpServletRequest request) {
    try {
      String refreshToken = extractRefreshTokenFromCookie(request);

      if (refreshToken == null) {
        log.debug("쿠키에서 Refresh Token을 찾을 수 없습니다");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "REFRESH_TOKEN_NOT_FOUND", "message", "리프레시 토큰이 없습니다."));
      }

      if (!jwtService.isValidRefreshToken(refreshToken)) {
        log.debug("유효하지 않은 Refresh Token입니다");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "INVALID_REFRESH_TOKEN", "message", "유효하지 않은 리프레시 토큰입니다."));
      }

      JwtService.TokenPair tokenPair = jwtService.refreshTokenPair(refreshToken);

      return ResponseEntity.ok(Map.of(
          "accessToken", tokenPair.getAccessToken(),
          "sessionId", tokenPair.getSessionId().toString()
      ));

    } catch (Exception e) {
      log.error("Access Token 가져오기 실패", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "TOKEN_REFRESH_FAILED", "message", "토큰 갱신에 실패했습니다."));
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
  public ResponseEntity<?> refreshToken(
      @CookieValue(value = "refreshToken", required = false) String refreshToken,
      HttpServletResponse response) {

    if (refreshToken == null || refreshToken.isEmpty()) {
      log.debug("Refresh Token이 없습니다");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "REFRESH_TOKEN_NOT_FOUND", "message", "리프레시 토큰이 없습니다."));
    }

    try {
      if (!jwtService.isValidRefreshToken(refreshToken)) {
        log.debug("유효하지 않은 Refresh Token입니다");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "INVALID_REFRESH_TOKEN", "message", "유효하지 않은 리프레시 토큰입니다."));
      }

      JwtService.TokenPair tokenPair = jwtService.refreshTokenPair(refreshToken);

      ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",
              tokenPair.getRefreshToken())
          .httpOnly(true)
          .secure(true)
          .path("/")
          .maxAge(7 * 24 * 60 * 60)
          .sameSite("Strict")
          .build();

      response.addHeader("Set-Cookie", refreshCookie.toString());
      return ResponseEntity.ok(Map.of(
          "accessToken", tokenPair.getAccessToken(),
          "sessionId", tokenPair.getSessionId()
      ));

    } catch (IllegalArgumentException e) {
      log.debug("토큰 갱신 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "TOKEN_REFRESH_FAILED", "message", e.getMessage()));
    } catch (Exception e) {
      log.error("토큰 갱신 중 예상치 못한 오류", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "INTERNAL_ERROR", "message", "토큰 갱신 중 오류가 발생했습니다."));
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    try {
      String refreshToken = extractRefreshTokenFromCookie(request);
      String accessToken = extractAccessTokenFromHeader(request);

      boolean logoutFlag = false;

      if (refreshToken != null && jwtService.isValidRefreshToken(refreshToken)) {
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        jwtService.logoutAll(userId);
        log.info("사용자 {}의 모든 세션이 무효회돠었습니다", userId);
        logoutFlag = true;
      }

      if (accessToken != null && jwtService.isValidAccessToken(accessToken)) {
        jwtService.logout(accessToken);
        log.info("개별 세션이 무효화되었습니다");
        logoutFlag = true;
      }

      invalidateRefreshTokenCookie(response);
      if (logoutFlag) {
        log.info("로그아웃 완료");
        return ResponseEntity.ok(Map.of("message", "로그아웃이 완료되었습니다."));
      } else {
        log.warn("유효한 토큰이 없어 로그아웃을 수행하지 않았습니다.");
        return ResponseEntity.ok(Map.of("message", "이미 로그아웃된 상태입니다."));
      }

    } catch (Exception e) {
      log.error("로그아웃 중 오류 발생", e);
      // 오류가 발생해도 쿠키는 제거
      invalidateRefreshTokenCookie(response);
      return ResponseEntity.ok(Map.of("message", "로그아웃이 완료되었습니다."));
    }
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> "refreshToken".equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }

  private String extractAccessTokenFromHeader(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

  private void invalidateRefreshTokenCookie(HttpServletResponse response) {
    ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0) // 즉시 만료
        .sameSite("Strict")
        .build();

    response.addHeader("Set-Cookie", expiredCookie.toString());
  }
}
