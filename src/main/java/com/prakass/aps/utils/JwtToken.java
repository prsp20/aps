package com.prakass.aps.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtToken {

    private final String secretKey;
    private final long accessTokenExpireOnMillSecond;
    private final long refreshTokenExpireOnMillSecond;

    public JwtToken(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expirationMs}") long accessTokenExpireOnMillSecond,
            @Value("${refresh.token.expire.millSecond}") long refreshTokenExpireOnMillSecond
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpireOnMillSecond = accessTokenExpireOnMillSecond;
        this.refreshTokenExpireOnMillSecond = refreshTokenExpireOnMillSecond;
    }

    private static final String ROLES = "roles";

    public String generateToken(String userName, List<String> roles, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLES, roles);
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(tokenType.contains("accessToken") ? new Date(System.currentTimeMillis() + accessTokenExpireOnMillSecond) : new Date(System.currentTimeMillis() + refreshTokenExpireOnMillSecond ))
                .signWith(getSignInKey())
                .compact();
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
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

        return extractClaim(token, claims ->  {
            Object roles = claims.get(ROLES);
            if(roles instanceof  List<?>)  {
                return ((List<?>) roles).stream().map(String::valueOf).toList();
            }
            return List.of();
        });
    }


}


