package com.prakass.aps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prakass.aps.common.exception.DuplicateEmailException;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.mapper.UserAccountMapper;
import com.prakass.aps.security.SessionJwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class AuthServiceTest {
  @Mock private UserAccountRepository userAccountRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private UserSessionService userSessionService;

  @Autowired private UserAccountMapper userAccountMapper;

  private AuthService authService;
  private UserSignupPayload userSignupPayload;
  private SessionJwtTokenUtil sessionJwtTokenUtil;
  private UserDetails userDetails;

  @BeforeEach
  public void setUp() {
    authService =
        new AuthService(
            userAccountRepository, passwordEncoder, userAccountMapper, userSessionService);
    userSignupPayload =
        UserSignupPayload.builder()
            .email("unique-mail@email.com")
            .password("password")
            .firstName("firstName")
            .lastName("lastName")
            .build();

    userDetails = User.builder().username("John-Doe@gmail.com").build();
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
    authService.loginUserAndCreateSessionToken(userDetails);

    UserAccountEntity userAccountEntity =
        UserAccountEntity.builder().username("user@mail.com").build();

    verify(userSessionService).createAndSaveUserSession(userDetails, userAccountEntity);
  }
}
