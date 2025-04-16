package com.prakass.aps.service;

import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dao.UserSessionRepository;
import com.prakass.aps.dto.LoginResponseWithToken;
import com.prakass.aps.dto.RefreshTokenPayload;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.UserSessionsEntity;
import com.prakass.aps.mapper.UserSessionMapper;
import com.prakass.aps.utils.JwtToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

  private final long accessTokenExpireTimeOnSecond;
  private final long refreshTokenExpireTimeOnSecond;
  private final UserAccountRepository userAccountRepository;
  private final JwtToken jwtToken;
  private UserSessionMapper userSessionMapper;
  private final UserSessionRepository userSessionRepository;

  public UserAccountService(
          @Value("${jwt.expirationMs}") long accessTokenExpireTimeOnSecond,
          @Value("${refresh.token.expire.millSecond}") long refreshTokenExpireTimeOnSecond,
          UserAccountRepository userAccountRepository,
          JwtToken jwtToken, UserSessionRepository userSessionRepository) {
    this.accessTokenExpireTimeOnSecond = accessTokenExpireTimeOnSecond;
    this.refreshTokenExpireTimeOnSecond = refreshTokenExpireTimeOnSecond;
    this.userAccountRepository = userAccountRepository;
    this.jwtToken = jwtToken;
    this.userSessionRepository = userSessionRepository;
  }

  public UserAccountEntity getUserAccountWithRoles(Long id) {
    UserAccountEntity userAccount = userAccountRepository.findById(id).get();
    userAccount.setRoles(userAccountRepository.findAllRolesByUserId(id));
    return userAccount;
  }

  public LoginResponseWithToken getUserLoginResponse(UserDetails userDetails) {
    List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    String accessToken = jwtToken.generateToken(userDetails.getUsername(), roles, accessTokenExpireTimeOnSecond);
    String refreshToken = jwtToken.generateToken(userDetails.getUsername(), roles, refreshTokenExpireTimeOnSecond);

    UserSessionsEntity userSessionsEntity = UserSessionsEntity.builder()
            .accessTokenGuid(UUID.randomUUID().toString())
            .refreshTokenGuid(UUID.randomUUID().toString())
            .revoked(false)
            .build();
    userSessionRepository.save(userSessionsEntity);
    return new LoginResponseWithToken(accessToken, refreshToken);
  }


  public LoginResponseWithToken generateRefreshToken(RefreshTokenPayload payload) {
    if (jwtToken.isTokenExpired(payload.refreshToken())) {
      throw new AuthException("Refresh token is expired ", HttpStatus.UNAUTHORIZED);
    }
    String username = jwtToken.extractUsername(payload.refreshToken());
    List<String> roles = jwtToken.extractRoles(payload.refreshToken());
    String accessToken = jwtToken.generateToken(username, roles, accessTokenExpireTimeOnSecond);
    String refreshToken = jwtToken.generateToken(username, roles, refreshTokenExpireTimeOnSecond);
    return new LoginResponseWithToken(accessToken, refreshToken);
  }
}
