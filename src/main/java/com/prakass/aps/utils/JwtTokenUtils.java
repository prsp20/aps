package com.prakass.aps.utils;

import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.dto.UserTokenDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class JwtTokenUtils {

  private final String secretKey;

  public JwtTokenUtils(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  private static final String ROLES = "roles";
  private static final String ACCESS_TOKEN_GUID = "accessTokenGuid";
  private static final String REFRESH_TOKEN_GUID = "refreshTokenGuid";

  public String generateToken(String userName, Set<String> roles, String accessGuid, String refreshGuid,  long expirationTime) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(ROLES, roles);
    claims.put(ACCESS_TOKEN_GUID, accessGuid);
    claims.put(REFRESH_TOKEN_GUID, refreshGuid);
    return Jwts.builder()
        .claims(claims)
        .subject(userName)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getSignInKey())
        .compact();
  }

  public UserTokenDetails verifyToken(String token) {
    try {
      Claims claims = Jwts.parser()
              .verifyWith(getSignInKey()) // use the same secret key as in generateToken
              .build()
              .parseSignedClaims(token)
              .getPayload();

      Set<String> roles = new HashSet<>();
      String userName = claims.get("subject", String.class);
      String accessTokenGuid  = claims.get(ACCESS_TOKEN_GUID, String.class);
      String refreshTokenGuid = claims.get(REFRESH_TOKEN_GUID, String.class);
      Object rolesClaims = claims.get(ROLES);
      if(rolesClaims instanceof Set<?>) {
        roles = ((Set<?>) rolesClaims).stream().map(String::valueOf).collect(Collectors.toSet());
      } else {
        roles = new HashSet<>();
      }

      return new UserTokenDetails(userName, roles, accessTokenGuid, refreshTokenGuid);


    } catch (Exception e) {
      throw  new AuthException(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = this.secretKey.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
