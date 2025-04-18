package com.prakass.aps.utils;

import static com.prakass.aps.service.JwtTokenService.*;
import static org.junit.jupiter.api.Assertions.*;

import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.dto.UserPasswordDetails;
import com.prakass.aps.dto.UserTokenDetails;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtTokenUtilsTest {

  private final String userName = "testUser";
  private final String accessGuid = UUID.randomUUID().toString();
  private final String refreshGuid = UUID.randomUUID().toString();
  private final long expiration = 3600L;
  private JwtTokenUtils jwtTokenUtils; // 1 hour

  @BeforeEach
  void setUp() {
    jwtTokenUtils = new JwtTokenUtils();
    String secret = "supersecretkeyforjwtshouldbeatleast256bitslong123!";
    ReflectionTestUtils.setField(jwtTokenUtils, "secretKey", secret);
  }

  @Test
  void testGenerateAndVerifyToken_Success() {
    Set<String> roles = new HashSet<>();
    roles.add("ADMIN");
    roles.add("USER");

    Map<String, Object> claims = new HashMap<>();
    claims.put(ACCESS_TOKEN_GUID, accessGuid);
    claims.put(REFRESH_TOKEN_GUID, refreshGuid);
    claims.put(ROLES, roles);

    String token = jwtTokenUtils.generateToken(userName, claims, expiration);
    assertNotNull(token);

    UserTokenDetails details = jwtTokenUtils.verifyToken(token);

    assertEquals(userName, details.userName());
    assertTrue(details.roles().contains("ADMIN"));
    assertTrue(details.roles().contains("USER"));
    assertEquals(accessGuid, details.accessTokenGuid());
    assertEquals(refreshGuid, details.refreshTokenGuid());
  }

  @Test
  void testVerifyResetPasswordToken_Success() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("passwordType", "RESET");

    String token = jwtTokenUtils.generateToken(userName, claims, expiration);
    assertNotNull(token);

    UserPasswordDetails details = jwtTokenUtils.verifyResetPasswordToken(token);
    assertEquals(userName, details.username());
    assertEquals("RESET", details.passwordType());
  }

  @Test
  void testInvalidToken_ThrowsException() {
    String invalidToken = "invalid.token.string";
    assertThrows(AuthException.class, () -> jwtTokenUtils.verifyToken(invalidToken));
  }

  @Test
  void testExpiredToken_ThrowsException() {
    Map<String, Object> claims = new HashMap<>();
    claims.put(ACCESS_TOKEN_GUID, accessGuid);
    claims.put(REFRESH_TOKEN_GUID, refreshGuid);

    // Generate token with -1 second expiration (already expired)
    String token = jwtTokenUtils.generateToken(userName, claims, -1);

    assertThrows(AuthException.class, () -> jwtTokenUtils.verifyToken(token));
  }
}
