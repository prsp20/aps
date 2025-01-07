package com.prakass.aps.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig{

  private AuthenticationProvider authenticationProvider;

  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    return new ProviderManager(authenticationProvider);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
    return http
            .authorizeHttpRequests(
              authorize -> authorize
                      .requestMatchers("/api/v1/auth").permitAll()
                      .anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .authenticationProvider(authenticationProvider)
            .build();
  }
}
