package com.prakass.aps.service;

import com.prakass.aps.common.exception.ResourceNotFoundException;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dao.UserSessionRepository;
import com.prakass.aps.dto.LoginResponse;
import com.prakass.aps.dto.RefreshTokenPayload;
import com.prakass.aps.dto.UserTokenDetails;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.UserSessionsEntity;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAccountService {


    private final UserAccountRepository userAccountRepository;
    private final JwtTokenService jwtTokenService;
    private final UserSessionRepository userSessionRepository;

    public UserAccountService(
            UserAccountRepository userAccountRepository,
            JwtTokenService jwtTokenService,
            UserSessionRepository userSessionRepository) {
        this.userAccountRepository = userAccountRepository;
        this.jwtTokenService = jwtTokenService;
        this.userSessionRepository = userSessionRepository;
    }

    public UserAccountEntity getUserAccountWithRoles(Long id) {
        UserAccountEntity userAccount = userAccountRepository.findById(id).get();
        userAccount.setRoles(userAccountRepository.findAllRolesByUserId(id));
        return userAccount;
    }

    @Transactional
    public LoginResponse getUserLoginResponse(UserDetails userDetails) {
        Set<String> roles =
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        UserSessionsEntity userSessionsEntity =
                UserSessionsEntity.builder()
                        .accessTokenGuid(UUID.randomUUID().toString())
                        .refreshTokenGuid(UUID.randomUUID().toString())
                        .revoked(false)
                        .build();
        UserSessionsEntity userSessionDB = userSessionRepository.save(userSessionsEntity);
        String accessToken =
                jwtTokenService.generateAccessToken(userDetails.getUsername(), roles, userSessionDB.getAccessTokenGuid(), userSessionDB.getRefreshTokenGuid());
        String refreshToken =
                jwtTokenService.generateRefreshToken(userDetails.getUsername(), roles, userSessionDB.getAccessTokenGuid(), userSessionDB.getRefreshTokenGuid());
        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse generateRefreshToken(RefreshTokenPayload payload) {
        UserTokenDetails userTokenDetails = jwtTokenService.userTokenDetails(payload.refreshToken());
        UserSessionsEntity userSessionsEntity = userSessionRepository.findUserSessionsEntitiesByAccessTokenGuidAndRefreshTokenGuid(userTokenDetails.accessTokenGuid(), userTokenDetails.refreshTokenGuid())
                .orElseThrow( () -> new ResourceNotFoundException("Could not find user session for refresh token", HttpStatus.BAD_REQUEST)) ;
        String accessToken = jwtTokenService.generateAccessToken(userTokenDetails.userName(), userTokenDetails.roles(), userSessionsEntity.getAccessTokenGuid(), userSessionsEntity.getRefreshTokenGuid());
        String refreshToken = jwtTokenService.generateAccessToken(userTokenDetails.userName(), userTokenDetails.roles(), userSessionsEntity.getAccessTokenGuid(), userSessionsEntity.getRefreshTokenGuid());
        userSessionsEntity.setAccessTokenGuid(userTokenDetails.accessTokenGuid());
        userSessionsEntity.setRefreshTokenGuid(userTokenDetails.refreshTokenGuid());
        userSessionRepository.save(userSessionsEntity);
        return new LoginResponse(accessToken, refreshToken);
    }
}
