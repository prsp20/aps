package com.prakass.aps.security;

import com.prakass.aps.dto.SessionJwtToken;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.UserSessionsEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class SessionJwtTokenUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expirationMs}")
  private long jwtExpirationInMs;

  public SessionJwtToken createSessionEntity(
      UserDetails userDetails, UserAccountEntity userAccountEntity) {
    String sessionGuid = UUID.randomUUID().toString();
    Instant createdAt = Instant.now();
    Instant expiresAt = Instant.now().plusMillis(jwtExpirationInMs);

    String jwtToken = generateToken(userDetails, sessionGuid, createdAt, expiresAt);

    UserSessionsEntity userSessionsEntity =
        UserSessionsEntity.builder()
            .sessionGuid(sessionGuid)
            .userAccount(userAccountEntity)
            .createdAt(createdAt)
            .expiresAt(expiresAt)
            .revoked(false)
            .build();
    return SessionJwtToken.builder()
        .userSessionsEntity(userSessionsEntity)
        .jwtToken(jwtToken)
        .build();
  }

  public String generateToken(
      UserDetails userDetails, String sessionGuid, Instant issuedAt, Instant expiresAt) {
    Key key = Keys.hmacShaKeyFor(secret.getBytes());

    Map<String, Object> claims = new HashMap<>();
    claims.put("username", userDetails.getUsername());
    claims.put("roles", userDetails.getAuthorities());
    claims.put("sessionGuid", sessionGuid);

    return Jwts.builder()
        .claims(claims)
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(expiresAt))
        .signWith(key)
        .compact();
  }
}
