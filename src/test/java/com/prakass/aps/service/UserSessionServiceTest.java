package com.prakass.aps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prakass.aps.dao.UserSessionRepository;
import com.prakass.aps.dto.SessionJwtToken;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.UserSessionsEntity;
import com.prakass.aps.security.SessionJwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public class UserSessionServiceTest {

  @InjectMocks private UserSessionService userSessionService;

  @Mock UserSessionRepository userSessionRepository;

  @Mock private SessionJwtTokenUtil sessionJwtTokenUtil;

  @Mock UserDetails userDetails;

  @Mock UserSessionsEntity userSessionsEntity;

  @Mock UserAccountEntity userAccountEntity;

  @Mock SessionJwtToken sessionJwtToken;

  String expectedToken;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    expectedToken = "test.jwt.token";
    UserSessionsEntity userSessionsEntity = UserSessionsEntity.builder().build();

    when(sessionJwtToken.userSessionsEntity()).thenReturn(userSessionsEntity);
    when(sessionJwtToken.jwtToken()).thenReturn(expectedToken);
    when(sessionJwtTokenUtil.createSessionEntity(userDetails, userAccountEntity))
        .thenReturn(sessionJwtToken);
  }

  @Test
  @Transactional
  public void createAndSaveUserSessionShouldCallJwtTokenUtilForValidUserDetails() {
    String token = userSessionService.createAndSaveUserSession(userDetails, userAccountEntity);

    verify(sessionJwtTokenUtil).createSessionEntity(userDetails, userAccountEntity);
  }

  @Test
  @Transactional
  public void createAndSaveUserSessionShouldCallUserSessionRepositoryForValidUserDetails() {
    userSessionService.createAndSaveUserSession(userDetails, userAccountEntity);

    verify(userSessionRepository).save(sessionJwtToken.userSessionsEntity());
  }

  @Test
  @Transactional
  public void createAndSaveUserSessionShouldReturnCorrectJwtToken() {
    String jwtToken = userSessionService.createAndSaveUserSession(userDetails, userAccountEntity);
    assertEquals(jwtToken, expectedToken);
  }
}
