package com.prakass.aps.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class AuthenticationProviderTest {
  @Mock private UserDetailsService userDetailsService;

  @Mock private PasswordEncoder passwordEncoder;

  private AuthenticationProvider authenticationProvider;

  private Authentication authToken;

  @BeforeEach
  public void setUp() {
    authenticationProvider = new CustomAuthenticationProvider(userDetailsService, passwordEncoder);

    authToken = new UsernamePasswordAuthenticationToken("john-doe@gmail.com", "password");

    UserDetails userDetails =
        User.builder().username("john-doe").password("encoded-password").build();

    when(userDetailsService.loadUserByUsername("john-doe@gmail.com")).thenReturn(userDetails);
    when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);
  }

  @Test
  public void testAuthenticate() {
    Authentication authentication = authenticationProvider.authenticate(authToken);

    assertTrue(authentication.isAuthenticated());
  }

  @Test
  public void testAuthenticateShouldThrowBadCredentialsExceptionWhenUserDetailsIsNull() {
    when(userDetailsService.loadUserByUsername("john@gmail.com")).thenReturn(null);

    assertThrows(
        BadCredentialsException.class,
        () ->
            authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken("john@gmail.com", "password")));
  }

  @Test
  public void testAuthenticateShouldThrowBadCredentialsExceptionWhenPasswordDoesNotMatches() {
    assertThrows(
        BadCredentialsException.class,
        () ->
            authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken("john-doe@gmail.com", "wrong-password")));
  }
}
