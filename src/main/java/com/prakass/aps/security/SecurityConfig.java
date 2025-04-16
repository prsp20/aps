package com.prakass.aps.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final ApplicationFilterChain applicationFilterChain;

    public SecurityConfig(AuthenticationProvider authenticationProvider,
                          ApplicationFilterChain applicationFilterChain) {
      this.authenticationProvider = authenticationProvider;
      this.applicationFilterChain = applicationFilterChain;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize -> {
                            authorize.requestMatchers("/api/v1/auth/login", "/api/v1/auth/signup").permitAll();
                            authorize.anyRequest().authenticated();
                        })
                .authenticationProvider(authenticationProvider)
                .sessionManagement(sessionManagement ->  sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(applicationFilterChain, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
