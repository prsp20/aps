package com.prakass.aps.service;

import com.prakass.aps.dto.UserTokenDetails;
import com.prakass.aps.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class JwtTokenService {

    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    private final JwtTokenUtils jwtTokenUtils;
    public  JwtTokenService(@Value("${jwt.access-token.expiration-seconds}") long accessTokenExpirationTime,
                            @Value("${jwt.refresh-token.expiration-seconds}") long refreshTokenExpirationTime,
                            JwtTokenUtils jwtTokenUtils){

        this.jwtTokenUtils = jwtTokenUtils;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }


    public String generateAccessToken(String userName, Set<String> role, String accessTokenGuid, String refreshTokenGuid) {
       return jwtTokenUtils.generateToken(userName, role, accessTokenGuid, refreshTokenGuid, accessTokenExpirationTime);
    }

    public String generateRefreshToken(String userName, Set<String> role, String accessTokenGuid, String refreshTokenGuid) {
        return jwtTokenUtils.generateToken(userName, role, accessTokenGuid, refreshTokenGuid, refreshTokenExpirationTime);
    }

    public UserTokenDetails userTokenDetails(String token) {
        return jwtTokenUtils.verifyToken(token);
    }

}
