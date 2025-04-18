package com.prakass.aps.utils;

import static com.prakass.aps.service.JwtTokenService.*;

import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.dto.UserPasswordDetails;
import com.prakass.aps.dto.UserTokenDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

  @Value("${jwt.secret}")
  private String secretKey;

  public String generateToken(
      String userName, Map<String, Object> claims, long expirationTimeInSecond) {
    try {
      return Jwts.builder()
          .claims(claims)
          .subject(userName)
          .issuedAt(DateUtils.convertZonedDateTimeToDate(DateUtils.getZonedDateTime()))
          .expiration(
              DateUtils.convertZonedDateTimeToDate(
                  DateUtils.getZonedDateTime().plusSeconds(expirationTimeInSecond)))
          .signWith(getSignInKey())
          .compact();
    } catch (Exception e) {
      throw new AuthException(e.getMessage());
    }
  }

  public UserTokenDetails verifyToken(String token) {
    Claims claims = getClaimsFromToken(token);

    Set<String> roles = new HashSet<>();

    String userName = claims.get("sub", String.class);
    String accessTokenGuid = claims.get(ACCESS_TOKEN_GUID, String.class);
    String refreshTokenGuid = claims.get(REFRESH_TOKEN_GUID, String.class);

    Object rolesClaims = claims.get(ROLES);
    if (rolesClaims instanceof List<?>) {
      roles = ((List<?>) rolesClaims).stream().map(String::valueOf).collect(Collectors.toSet());
    } else {
      roles = new HashSet<>();
    }

    return new UserTokenDetails(userName, roles, accessTokenGuid, refreshTokenGuid);
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = this.secretKey.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    } catch (Exception e) {
      throw new AuthException(e.getMessage());
    }
  }

  public UserPasswordDetails verifyResetPasswordToken(String token) {
    Claims claims = getClaimsFromToken(token);
    final String name = claims.get("sub", String.class);
    final String passwordType = claims.get("passwordType", String.class);
    return new UserPasswordDetails(name, passwordType);
  }
}
