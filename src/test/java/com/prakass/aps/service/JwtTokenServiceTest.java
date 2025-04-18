package com.prakass.aps.service;

import static com.prakass.aps.service.JwtTokenService.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prakass.aps.dto.TokenType;
import com.prakass.aps.dto.UserPasswordDetails;
import com.prakass.aps.dto.UserTokenDetails;
import com.prakass.aps.utils.JwtTokenUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class JwtTokenServiceTest {
  @Mock private JwtTokenUtils jwtTokenUtils;

  @InjectMocks private JwtTokenService jwtTokenService;

  @BeforeEach
  void setUp() {
    // Set the expiration times via reflection to ensure consistent values
    ReflectionTestUtils.setField(jwtTokenService, "accessTokenExpirationTime", 3600L);
    ReflectionTestUtils.setField(jwtTokenService, "refreshTokenExpirationTime", 7200L);
    ReflectionTestUtils.setField(jwtTokenService, "passwordResetExpirationTime", 1800L);
  }

  @Test
  void testGenerateAccessToken() {
    String username = "user@example.com";
    Set<String> roles = Set.of("ROLE_USER");
    String accessTokenGuid = UUID.randomUUID().toString();
    String expectedToken = "access.jwt.token";

    Map<String, Object> claims = new HashMap<>();
    claims.put(ROLES, roles);
    claims.put(ACCESS_TOKEN_GUID, accessTokenGuid);

    when(jwtTokenUtils.generateToken(eq(username), eq(claims), eq(3600L)))
        .thenReturn(expectedToken);

    String result = jwtTokenService.generateAccessToken(username, roles, accessTokenGuid);

    assertEquals(expectedToken, result);
    verify(jwtTokenUtils).generateToken(eq(username), eq(claims), eq(3600L));
  }

  @Test
  void testGenerateRefreshToken() {
    String username = "user@example.com";
    Set<String> roles = Set.of("ROLE_USER");
    String refreshTokenGuid = UUID.randomUUID().toString();
    String expectedToken = "refresh.jwt.token";

    Map<String, Object> claims = new HashMap<>();
    claims.put(ROLES, roles);
    claims.put(REFRESH_TOKEN_GUID, refreshTokenGuid);

    when(jwtTokenUtils.generateToken(eq(username), eq(claims), eq(7200L)))
        .thenReturn(expectedToken);

    String result = jwtTokenService.generateRefreshToken(username, roles, refreshTokenGuid);

    assertEquals(expectedToken, result);
    verify(jwtTokenUtils).generateToken(eq(username), eq(claims), eq(7200L));
  }

  @Test
  void testGeneratePasswordResetToken() {
    String email = "user@example.com";
    String expectedToken = "password-reset.jwt.token";

    Map<String, Object> expectedClaims = new HashMap<>();
    expectedClaims.put(PASSWORD_TYPE, TokenType.RESET_PASSWORD.getType());

    when(jwtTokenUtils.generateToken(eq(email), eq(expectedClaims), eq(1800L)))
        .thenReturn(expectedToken);

    String result = jwtTokenService.generatePasswordResetToken(email);

    assertEquals(expectedToken, result);
    verify(jwtTokenUtils).generateToken(eq(email), eq(expectedClaims), eq(1800L));
  }

  @Test
  void testUserTokenDetails() {
    String token = "some.jwt.token";
    Set<String> roles = Set.of("ROLE_USER");
    String accessTokenGuid = UUID.randomUUID().toString();
    String refreshTokenGuid = UUID.randomUUID().toString();

    UserTokenDetails expectedDetails =
        new UserTokenDetails("user@example.com", roles, accessTokenGuid, refreshTokenGuid);

    when(jwtTokenUtils.verifyToken(token)).thenReturn(expectedDetails);

    UserTokenDetails result = jwtTokenService.userTokenDetails(token);

    assertEquals(expectedDetails, result);
    verify(jwtTokenUtils).verifyToken(token);
  }

  @Test
  void testVerifyResetPasswordToken() {
    String token = "reset.jwt.token";
    UserPasswordDetails expectedDetails =
        new UserPasswordDetails("user@example.com", TokenType.RESET_PASSWORD.getType());

    when(jwtTokenUtils.verifyResetPasswordToken(token)).thenReturn(expectedDetails);

    UserPasswordDetails result = jwtTokenService.verifyResetPasswordToken(token);

    assertEquals(expectedDetails, result);
    verify(jwtTokenUtils).verifyResetPasswordToken(token);
  }
}
