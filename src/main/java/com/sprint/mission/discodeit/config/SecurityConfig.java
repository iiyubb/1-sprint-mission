package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.AuthenticationFilter;
import com.sprint.mission.discodeit.security.CustomUserDetailsService;
import com.sprint.mission.discodeit.security.JsonAuthenticationFailureHandler;
import com.sprint.mission.discodeit.security.JsonAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final ObjectMapper objectMapper;
  private final UserMapper userMapper;
  private final CustomUserDetailsService userDetailsService;

  @Bean
  SecurityFilterChain chain(HttpSecurity http) throws Exception {
    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    requestHandler.setCsrfRequestAttributeName("_csrf");

    CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    tokenRepository.setCookieName("CSRF-TOKEN");
    tokenRepository.setHeaderName("X-CSRF-TOKEN");

    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(tokenRepository)
            .csrfTokenRequestHandler(requestHandler)
            .ignoringRequestMatchers("/api/auth/logout"))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/css/**", "/images/**", "/js/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers("/api/auth/me").authenticated()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().authenticated())
        .securityContext(securityContext -> securityContext.securityContextRepository(
            securityContextRepository()))
        .httpBasic(Customizer.withDefaults())
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(1)
            .expiredUrl("/login?expired"));

    SecurityFilterChain filterChain = http.build();
    return filterChain;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());

    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(authenticationProvider());
  }

  @Bean
  public AuthenticationFilter authenticationFilter() {
    AuthenticationFilter filter = new AuthenticationFilter(authenticationManager(), objectMapper);
    filter.setAuthenticationSuccessHandler(
        new JsonAuthenticationSuccessHandler(objectMapper, userMapper));
    filter.setAuthenticationFailureHandler(new JsonAuthenticationFailureHandler(objectMapper));
    filter.setSecurityContextRepository(securityContextRepository());
    return filter;
  }

  @Bean
  public LogoutHandler logoutHandler() {
    CompositeLogoutHandler logoutHandler = new CompositeLogoutHandler(
        new SecurityContextLogoutHandler(),
        new CookieClearingLogoutHandler("JSESSIONID", "CSRF-TOKEN")
    );
    return logoutHandler;
  }
}