package com.prakass.aps.service;

import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.common.exception.BadRequestException;
import com.prakass.aps.common.exception.DuplicateEmailException;
import com.prakass.aps.common.exception.ResourceNotFoundException;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dao.UserSessionRepository;
import com.prakass.aps.dto.*;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.UserSessionsEntity;
import com.prakass.aps.mapper.UserAccountMapper;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class AuthService {
  private UserAccountRepository userAccountRepository;
  private PasswordEncoder passwordEncoder;
  private UserAccountMapper userAccountMapper;
  private final UserSessionService userSessionService;
  private final JwtTokenService jwtTokenService;
  private final UserSessionRepository userSessionRepository;

  @Transactional
  public SignUpResponsePayload signUpUser(UserSignupPayload userSignupPayload) {
    if (userAccountRepository.existsByEmail(userSignupPayload.email())) {
      throw new DuplicateEmailException("Email Already Exists");
    }
    UserAccountEntity userAccount =
        userAccountMapper.userSignupPayloadToUserAccountEntity(userSignupPayload);
    userAccount.setPasswordHash(passwordEncoder.encode(userSignupPayload.password()));
    userAccountRepository.save(userAccount);

    return userAccountMapper.userAccountEntityToSignUpResponsePayload(userAccount);
  }

  @Transactional
  public String loginUserAndCreateSessionToken(UserDetails userDetails) {
    UserAccountEntity userAccount =
        userAccountRepository.findFirstByEmail(userDetails.getUsername());

    return userSessionService.createAndSaveUserSession(userDetails, userAccount);
  }

  @Transactional
  public LoginResponse getUserLoginResponse(UserDetails userDetails) {
    Set<String> roles =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

    UserSessionsEntity userSessionsEntity =
        UserSessionsEntity.builder()
            .accessTokenGuid(UUID.randomUUID().toString())
            .refreshTokenGuid(UUID.randomUUID().toString())
            .revoked(false)
            .email(userDetails.getUsername())
            .build();

    UserSessionsEntity userSessionFromDB = userSessionRepository.save(userSessionsEntity);

    String accessToken =
        jwtTokenService.generateAccessToken(
            userDetails.getUsername(), roles, userSessionFromDB.getAccessTokenGuid());

    String refreshToken =
        jwtTokenService.generateRefreshToken(
            userDetails.getUsername(), roles, userSessionFromDB.getRefreshTokenGuid());

    return new LoginResponse(accessToken, refreshToken);
  }

  @Transactional
  public LoginResponse generateRefreshToken(RefreshTokenPayload payload) {
    UserTokenDetails userTokenDetails = jwtTokenService.userTokenDetails(payload.refreshToken());

    UserSessionsEntity userSessionsEntity =
        userSessionRepository
            .findUserSessionsEntitiesByRefreshTokenGuid(userTokenDetails.refreshTokenGuid())
            .orElseThrow(() -> new AuthException("Could not find user session for refresh token."));

    String accessToken =
        jwtTokenService.generateAccessToken(
            userTokenDetails.userName(),
            userTokenDetails.roles(),
            userSessionsEntity.getAccessTokenGuid());

    String refreshToken =
        jwtTokenService.generateAccessToken(
            userTokenDetails.userName(),
            userTokenDetails.roles(),
            userSessionsEntity.getRefreshTokenGuid());

    userSessionsEntity.setAccessTokenGuid(userTokenDetails.accessTokenGuid());
    userSessionsEntity.setRefreshTokenGuid(userTokenDetails.refreshTokenGuid());

    userSessionRepository.save(userSessionsEntity);

    return new LoginResponse(accessToken, refreshToken);
  }

  public String requestPasswordReset(RequestPasswordResetPayload payload) {
    UserAccountEntity userAccount = userAccountRepository.findFirstByEmail(payload.email());

    String passwordResetToken = jwtTokenService.generatePasswordResetToken(payload.email());

    // todo send email
    return passwordResetToken;
  }

  @Transactional
  public void resetPassword(PasswordRequestPayload payload) {
    if (!payload.newPassword().equals(payload.confirmPassword())) {
      throw new BadRequestException("Password does not contain confirm password.");
    }

    UserPasswordDetails userPasswordDetails =
        jwtTokenService.verifyResetPasswordToken(payload.token());
    if (!TokenType.RESET_PASSWORD.getType().equals(userPasswordDetails.passwordType())) {
      throw new AuthException("Invalid token.");
    }

    UserAccountEntity userAccount =
        userAccountRepository.findFirstByEmail(userPasswordDetails.username());
    if (userAccount == null) {
      throw new ResourceNotFoundException("Could not find user account.");
    }

    userAccount.setPasswordHash(passwordEncoder.encode(payload.newPassword()));
    userAccountRepository.save(userAccount);
  }
}
