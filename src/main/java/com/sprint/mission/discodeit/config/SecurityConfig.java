package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain chain(HttpSecurity http) throws Exception {
    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    requestHandler.setCsrfRequestAttributeName("_csrf");

    CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    tokenRepository.setCookieName("CSRF-TOKEN");
    tokenRepository.setHeaderName("X-CSRF-TOKEN");

    http
        .csrf(csrf -> csrf.csrfTokenRepository(tokenRepository)
            .csrfTokenRequestHandler(requestHandler))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/css/**", "/images/**", "/js/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/api/auth/csrf-token").permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults())
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable);

    SecurityFilterChain filterChain = http.build();
    return filterChain;
  }
}
