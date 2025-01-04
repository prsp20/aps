package com.prakass.aps.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

  @Autowired
  private AuthenticationProvider authenticationProvider;

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

//  @Bean
//  public AuthenticationManager authManager(HttpSecurity http) throws Exception {
////    AuthenticationManagerBuilder authenticationManagerBuilder =
////            http.getSharedObject(AuthenticationManagerBuilder.class);
////    authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
////    return authenticationManagerBuilder.build();
//
//    return super.A;
//  }
}
