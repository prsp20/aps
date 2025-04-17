package com.prakass.aps.utils;

import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.dto.UserPasswordDetails;
import com.prakass.aps.dto.UserTokenDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.prakass.aps.service.JwtTokenService.*;


@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secretKey;
    private final DateUtils dateUtils;


    public String generateToken(String userName, Map<String, Object> claims, long expirationTimeInSecond) {
        try {
            return Jwts.builder()
                    .claims(claims)
                    .subject(userName)
                    .issuedAt(dateUtils.convertZonedDateTimeToDate(dateUtils.getZonedDateTime()))
                    .expiration(dateUtils.convertZonedDateTimeToDate(dateUtils.getZonedDateTime().plusSeconds(expirationTimeInSecond)))
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
        if (rolesClaims instanceof Set<?>) {
            roles = ((Set<?>) rolesClaims).stream().map(String::valueOf).collect(Collectors.toSet());
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
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
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
