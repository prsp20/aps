package com.prakass.aps.service;

import com.prakass.aps.dao.AccessTokenRepository;
import com.prakass.aps.dao.RefreshTokenRepository;
import com.prakass.aps.entities.token.AccessToken;
import com.prakass.aps.entities.token.RefreshToken;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.mapper.TokenMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenMapper tokenMapper;

    public TokenService(AccessTokenRepository accessTokenRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        TokenMapper tokenMapper) {
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenMapper = tokenMapper;
    }

    @Transactional
    public String createAccessToken(String token, UserAccountEntity userAccountEntity) {
//        AccessToken accessToken = tokenMapper.accessTokenToEntity(token, userAccountEntity);
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setUserAccountEntity(userAccountEntity);
        accessToken.setExpired(false);
        AccessToken accessTokenDB = accessTokenRepository.save(accessToken);
        return accessTokenDB.getToken();
    }

    @Transactional
    public String createRefreshToken(String token, UserAccountEntity userAccountEntity) {

//        RefreshToken refreshToken = refreshTokenRepository.save(tokenMapper.refreshTokenEntity(token, userAccountEntity));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserAccountEntity(userAccountEntity);
        refreshToken.setExpired(false);
        RefreshToken refreshTokenDB = refreshTokenRepository.save(refreshToken);
        return refreshTokenDB.getToken();
    }
}
