package com.prakass.aps.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expirationMs}")
  private long jwtExpirationInMs;

  public String generateToken(UserDetails userDetails) {
    Key key = Keys.hmacShaKeyFor(secret.getBytes());

    Map<String, Object> claims = new HashMap<>();
    claims.put("guid", userDetails.getUsername());
    claims.put("roles", userDetails.getAuthorities());

    return Jwts.builder()
        .claims(claims)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
        .signWith(key)
        .compact();
  }
}
