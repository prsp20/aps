package com.prakass.aps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    int saltLength = 16;
    int hashLength = 32;
    int parallelism = 1;
    int memory = 65536;
    int iterations = 1;
    return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            authorize ->
                authorize.requestMatchers("/api/v1/auth").permitAll().anyRequest().permitAll())
        .csrf(csrf -> csrf.disable())
        .build();
  }
}
