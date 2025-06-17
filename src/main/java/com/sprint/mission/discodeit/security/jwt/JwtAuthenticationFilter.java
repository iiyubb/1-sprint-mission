package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final PathMatcher pathMatcher = new AntPathMatcher();

  private final List<String> skipAuthenticationPaths = Arrays.asList(
      "/api/auth/login",
      "/api/auth/refresh",
      "/api/auth/csrf-token",
      "/api/users",
      "/css/**",
      "/images/**",
      "/js/**",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "/actuator/**",
      "/api/public/**"
  );

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (shouldSkipAuthentication(request)) {
      log.debug("JWT 인증을 건너뛰는 경로: {}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    String token = getTokenFromRequest(request);

    if (StringUtils.hasText(token) && jwtService.isValidAccessToken(token)) {
      UUID userId = jwtService.getUserIdFromToken(token);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private boolean shouldSkipAuthentication(HttpServletRequest request) {
    String requestPath = request.getRequestURI();
    String method = request.getMethod();

    for (String skipPath : skipAuthenticationPaths) {
      if (pathMatcher.match(skipPath, requestPath)) {
        if (skipPath.equals("/api/users") && !"POST".equals(method)) {
          continue;
        }
        return true;
      }
    }
    return false;
  }
}
