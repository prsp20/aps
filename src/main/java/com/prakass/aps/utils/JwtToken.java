package com.prakass.aps.utils;

import com.prakass.aps.common.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtToken {

  private final String secretKey;

  public JwtToken(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  private static final String ROLES = "roles";

  public String generateToken(String userName, List<String> roles, long expirationTime) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(ROLES, roles);
    return Jwts.builder()
        .claims(claims)
        .subject(userName)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getSignInKey())
        .compact();
  }

  public Claims verifyToken(String token) {
      return Jwts
              .parser()
              .verifyWith(getSignInKey())// use the same secret key as in generateToken
              .build()
              .parseSignedClaims(token)
              .getPayload();
  }


  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = this.secretKey.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public boolean isTokenExpired(String token) {
    final Date expiration = extractExpiration(token);
    return expiration.before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public List<String> extractRoles(String token) {
    return extractClaim(
        token,
        claims -> {
          Object roles = claims.get(ROLES);
          if (roles instanceof List<?>) {
            return ((List<?>) roles).stream().map(String::valueOf).toList();
          }
          return List.of();
        });
  }
}
