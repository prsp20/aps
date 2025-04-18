package com.prakass.aps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.common.exception.DuplicateEmailException;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dao.UserSessionRepository;
import com.prakass.aps.dto.LoginResponse;
import com.prakass.aps.dto.RefreshTokenPayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.dto.UserTokenDetails;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.UserSessionsEntity;
import com.prakass.aps.mapper.UserAccountMapper;
import com.prakass.aps.security.SessionJwtTokenUtil;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
  @Mock private UserAccountRepository userAccountRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private UserSessionService userSessionService;

  @Autowired private UserAccountMapper userAccountMapper;

  @Mock private JwtTokenService jwtTokenService;

  @Mock private UserSessionRepository userSessionRepository;

  @InjectMocks private AuthService authService;
  private UserSignupPayload userSignupPayload;
  private SessionJwtTokenUtil sessionJwtTokenUtil;
  private UserDetails userDetails;

  @BeforeEach
  public void setUp() {
    authService =
        new AuthService(
            userAccountRepository,
            passwordEncoder,
            userAccountMapper,
            userSessionService,
            jwtTokenService,
            userSessionRepository);
    userSignupPayload =
        UserSignupPayload.builder()
            .email("unique-mail@email.com")
            .password("password")
            .firstName("firstName")
            .lastName("lastName")
            .build();

    userDetails =
        User.builder()
            .username("unique-mail@email.com")
            .roles("ADMIN")
            .password("Test@123")
            .build();
  }

  @Test
  @Transactional
  public void signUpShouldCallSaveMethodForSignUpWithUniqueEmail() {
    when(userAccountRepository.existsByEmail(userSignupPayload.email())).thenReturn(Boolean.FALSE);

    authService.signUpUser(userSignupPayload);
    verify(userAccountRepository).save(any(UserAccountEntity.class));
  }

  @Test
  @Transactional
  public void signUpShouldThrowDuplicateEmailExceptionForSignUpWithExistingEmail() {
    when(userAccountRepository.existsByEmail(userSignupPayload.email())).thenReturn(Boolean.TRUE);

    assertThatThrownBy(() -> authService.signUpUser(userSignupPayload))
        .isInstanceOf(DuplicateEmailException.class)
        .hasMessageContaining("Email Already Exists");
  }

  @Test
  @Transactional
  public void signUpShouldCallPasswordEncoderForSignUpWithUniqueEmail() {
    when(userAccountRepository.existsByEmail(userSignupPayload.email())).thenReturn(Boolean.FALSE);

    authService.signUpUser(userSignupPayload);
    verify(passwordEncoder).encode(userSignupPayload.password());
  }

  @Test
  @Transactional
  public void loginShouldFetchUserDetails() {
    authService.loginUserAndCreateSessionToken(userDetails);

    verify(userAccountRepository).findFirstByEmail(userDetails.getUsername());
  }

  @Test
  @Transactional
  public void loginShouldCallUserSessionServiceForValidUserDetails() {
    UserAccountEntity userAccountEntity =
        UserAccountEntity.builder().username("unique-mail@email.com").passwordHash("test").build();
    when(userAccountRepository.findFirstByEmail(userDetails.getUsername()))
        .thenReturn(userAccountEntity);
    authService.loginUserAndCreateSessionToken(userDetails);
    verify(userSessionService).createAndSaveUserSession(userDetails, userAccountEntity);
  }

  @Test
  public void testGenerateRefreshToken_Success() {
    // Arrange
    String username = "unique-mail@email.com";
    Set<String> roles = Set.of("ROLE_USER");
    String accessTokenGuid = UUID.randomUUID().toString();
    String refreshTokenGuid = UUID.randomUUID().toString();

    RefreshTokenPayload refreshTokenPayload = new RefreshTokenPayload("refresh-token-value");
    UserTokenDetails userTokenDetails =
        new UserTokenDetails(username, roles, accessTokenGuid, refreshTokenGuid);

    UserSessionsEntity userSessionsEntity = new UserSessionsEntity();
    userSessionsEntity.setAccessTokenGuid(accessTokenGuid);
    userSessionsEntity.setRefreshTokenGuid(refreshTokenGuid);

    // Mocking repository and service calls
    when(jwtTokenService.userTokenDetails(refreshTokenPayload.refreshToken()))
        .thenReturn(userTokenDetails);
    when(userSessionRepository.findUserSessionsEntitiesByRefreshTokenGuid(refreshTokenGuid))
        .thenReturn(Optional.of(userSessionsEntity));
    when(jwtTokenService.generateAccessToken(username, roles, accessTokenGuid))
        .thenReturn("new-access-token");
    when(jwtTokenService.generateAccessToken(username, roles, refreshTokenGuid))
        .thenReturn("new-refresh-token");

    // Act
    LoginResponse response = authService.generateRefreshToken(refreshTokenPayload);

    // Assert
    assertNotNull(response);
    assertEquals("new-access-token", response.accessToken());
    assertEquals("new-refresh-token", response.refreshToken());

    // Verify that repository save was called
    verify(userSessionRepository).save(userSessionsEntity);
  }

  @Test
  public void testGenerateRefreshToken_UserSessionNotFound() {
    String username = "unique-mail@email.com";
    Set<String> roles = Set.of("ROLE_USER");
    String accessTokenGuid = UUID.randomUUID().toString();
    String refreshTokenGuid = UUID.randomUUID().toString();

    RefreshTokenPayload refreshTokenPayload = new RefreshTokenPayload("refresh-token-value");
    UserTokenDetails userTokenDetails =
        new UserTokenDetails(username, roles, accessTokenGuid, refreshTokenGuid);

    when(jwtTokenService.userTokenDetails(refreshTokenPayload.refreshToken()))
        .thenReturn(userTokenDetails);
    when(userSessionRepository.findUserSessionsEntitiesByRefreshTokenGuid(refreshTokenGuid))
        .thenReturn(Optional.empty());

    AuthException exception =
        assertThrows(
            AuthException.class,
            () -> {
              authService.generateRefreshToken(refreshTokenPayload);
            });

    assertEquals("Could not find user session for refresh token.", exception.getMessage());

    verify(userSessionRepository, never()).save(any(UserSessionsEntity.class));
  }

  @Test
  public void testGetUserLoginResponse_Success() {
    // Arrange
    String username = "unique-mail@email.com";

    ArgumentCaptor<UserSessionsEntity> sessionCaptor =
        ArgumentCaptor.forClass(UserSessionsEntity.class);

    UserSessionsEntity savedUserSession = new UserSessionsEntity();
    savedUserSession.setAccessTokenGuid(UUID.randomUUID().toString());
    savedUserSession.setRefreshTokenGuid(UUID.randomUUID().toString());
    savedUserSession.setEmail(username);
    savedUserSession.setRevoked(false);

    when(userSessionRepository.save(sessionCaptor.capture())).thenReturn(savedUserSession);

    when(jwtTokenService.generateAccessToken(anyString(), anySet(), anyString()))
        .thenReturn("mocked-access-token");
    when(jwtTokenService.generateRefreshToken(anyString(), anySet(), anyString()))
        .thenReturn("mocked-refresh-token");

    LoginResponse response = authService.getUserLoginResponse(userDetails);

    assertNotNull(response);
    assertEquals("mocked-access-token", response.accessToken());
    assertEquals("mocked-refresh-token", response.refreshToken());

    UserSessionsEntity capturedSession = sessionCaptor.getValue();
    assertEquals(username, capturedSession.getEmail());
    assertFalse(capturedSession.isRevoked());
    assertNotNull(capturedSession.getAccessTokenGuid());
    assertNotNull(capturedSession.getRefreshTokenGuid());

    verify(jwtTokenService)
        .generateAccessToken(eq(username), anySet(), eq(savedUserSession.getAccessTokenGuid()));
    verify(jwtTokenService)
        .generateRefreshToken(eq(username), anySet(), eq(savedUserSession.getRefreshTokenGuid()));
  }
}
