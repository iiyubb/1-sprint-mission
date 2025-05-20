package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final LogoutHandler logoutHandler;
  private final SecurityContextRepository contextRepository;
  private final UserMapper userMapper;

  @GetMapping("/csrf-token")
  public CsrfToken getCsrfToken(CsrfToken csrfToken) {
    return csrfToken;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getUserDto(Authentication authentication) {

    log.info("현재 사용자 정보 조회: {}", authentication != null ? authentication.getName() : "인증되지 않음");

    if (authentication != null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      log.warn("인증되지 않은 사용자의 접근");
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User user = userDetails.getUser();

    UserDto userDto = userMapper.toDto(user);

    log.info("현재 사용자 정보 조회 완료: email={}", user.getEmail());
    return ResponseEntity.ok(userDto);
  }

  @PostMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    log.info("로그아웃 요청: {}", authentication != null ? authentication.getName() : "익명 사용자");

    logoutHandler.logout(request, response, authentication);
    SecurityContextHolder.clearContext();
    SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
    contextRepository.saveContext(emptyContext, request, response);

    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
      log.debug("세선 무효화 완료: {}", session.getId());
    }

    ResponseCookie delete = ResponseCookie.from("JSESSIONID", "")
        .path("/")
        .maxAge(0)
        .secure(true)
        .httpOnly(true)
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, delete.toString());

    log.info("로그아웃 처리 완료");
  }
}
