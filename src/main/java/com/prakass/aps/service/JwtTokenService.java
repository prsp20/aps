package com.prakass.aps.service;

import com.prakass.aps.dto.TokenType;
import com.prakass.aps.dto.UserPasswordDetails;
import com.prakass.aps.dto.UserTokenDetails;
import com.prakass.aps.utils.JwtTokenUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

  public static final String ROLES = "roles";
  public static final String ACCESS_TOKEN_GUID = "accessTokenGuid";
  public static final String REFRESH_TOKEN_GUID = "refreshTokenGuid";
  public static final String PASSWORD_TYPE = "passwordType";

  @Value("${jwt.access-token.expiration-seconds}")
  public long accessTokenExpirationTime;

  @Value("${jwt.refresh-token.expiration-seconds}")
  private long refreshTokenExpirationTime;

  @Value("${jwt.password-reset-token-expiration-seconds}")
  private long passwordResetExpirationTime;

  private final JwtTokenUtils jwtTokenUtils;

  public String generateAccessToken(String userName, Set<String> roles, String accessTokenGuid) {
    Map<String, Object> claims = new HashMap<>();

    claims.put(ROLES, roles);
    claims.put(ACCESS_TOKEN_GUID, accessTokenGuid);

    return jwtTokenUtils.generateToken(userName, claims, accessTokenExpirationTime);
  }

  public String generateRefreshToken(String userName, Set<String> roles, String refreshTokenGuid) {
    Map<String, Object> claims = new HashMap<>();

    claims.put(ROLES, roles);
    claims.put(REFRESH_TOKEN_GUID, refreshTokenGuid);

    return jwtTokenUtils.generateToken(userName, claims, refreshTokenExpirationTime);
  }

  public UserTokenDetails userTokenDetails(String token) {
    return jwtTokenUtils.verifyToken(token);
  }

  public String generatePasswordResetToken(String email) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(PASSWORD_TYPE, TokenType.RESET_PASSWORD);
    return jwtTokenUtils.generateToken(email, claims, passwordResetExpirationTime);
  }

  public UserPasswordDetails verifyResetPasswordToken(String token) {
    return jwtTokenUtils.verifyResetPasswordToken(token);
  }
}
