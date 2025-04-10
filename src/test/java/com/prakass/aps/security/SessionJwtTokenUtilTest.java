package com.prakass.aps.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.prakass.aps.dto.SessionJwtToken;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

public class SessionJwtTokenUtilTest {
  SessionJwtTokenUtil sessionJwtTokenUtil;

  @Mock private UserDetails userDetails;

  @Mock private UserAccountEntity userAccountEntity;

  private String sessionGuid;
  private Instant issuedAt;
  private Instant expiresAt;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    sessionJwtTokenUtil = new SessionJwtTokenUtil();

    String secret = "test-secret-123456-123456-123456";
    long jwtExpirationInMs = 60000L;

    ReflectionTestUtils.setField(sessionJwtTokenUtil, "secret", secret);
    ReflectionTestUtils.setField(sessionJwtTokenUtil, "jwtExpirationInMs", jwtExpirationInMs);

    sessionGuid = "session-123";
    issuedAt = Instant.now();
    expiresAt = Instant.now().plusSeconds(10);
  }

  @Test
  public void generateTokenShouldReturnValidJwtToken() {
    String jwtToken =
        sessionJwtTokenUtil.generateToken(userDetails, sessionGuid, issuedAt, expiresAt);

    assertNotNull(jwtToken);
    assertEquals(3, jwtToken.split("\\.").length);
  }

  @Test
  public void createSessionEntityShouldReturnValidSessionJwtToken() {
    SessionJwtToken sessionJwtToken =
        sessionJwtTokenUtil.createSessionEntity(userDetails, userAccountEntity);

    assertNotNull(sessionJwtToken);
    assertNotNull(sessionJwtToken.jwtToken());
    assertNotNull(sessionJwtToken.userSessionsEntity());
  }
}
