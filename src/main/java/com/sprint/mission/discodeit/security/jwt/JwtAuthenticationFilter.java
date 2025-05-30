package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  private static final List<String> AUTHENTICATED_PATHS = Arrays.asList(
      "/api/auth/me",
      "/api/auth/role",
      "/api/users",
      "/api/channels",
      "/api/messages"
  );

  private static final List<String> PUBLIC_PATHS = Arrays.asList(
      "/api/auth/login",
      "api/auth/csrf-token"
  );

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestPath = request.getRequestURI();
    String method = request.getMethod();

    if (!isAuthenticationRequired(requestPath, method)) {
      log.debug("인증이 필요하지 않은 경로: {}", requestPath);
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String accessToken = extractTokenFromRequest(request);

      if (accessToken == null) {
        log.warn("액세스 토큰이 없음: {}", requestPath);
        sendUnauthorizedResponse(response, "액세스 토큰이 필요합니다.");
        return;
      }

      if (!jwtService.validateToken(accessToken)) {
        log.warn("유효하지 않은 액세스 토큰: {}", requestPath);
        sendUnauthorizedResponse(response, "유효하지 않은 액세스 토큰입니다.");
        return;
      }

      UserDto userDto = jwtService.extractUserDto(accessToken);

      if (userDto != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.username());

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("인증 완료: 사용자 = {}", userDto.username());
      }
    } catch (Exception e) {
      log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage(), e);
      sendUnauthorizedResponse(response, "인증 처리 중 오류가 발생했습니다.");
      return;
    }
    filterChain.doFilter(request, response);
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && !bearerToken.trim().isEmpty() && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private boolean isAuthenticationRequired(String requestPath, String method) {
    if (PUBLIC_PATHS.stream().anyMatch(requestPath::startsWith)) {
      return false;
    }

    if (AUTHENTICATED_PATHS.stream().anyMatch(requestPath::startsWith)) {
      return true;
    }

    if ("GET".equals(method)) {
      return false;
    }

    return requestPath.startsWith("/api");
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message)
      throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    Map<String, String> errorResponse = Map.of(
        "error", "UNAUTHORIZED",
        "message", message
    );

    String jsonResponse = objectMapper.writeValueAsString(errorResponse);
    response.getWriter().write(jsonResponse);
  }
}
