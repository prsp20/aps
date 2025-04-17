package com.prakass.aps.service;

import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.common.exception.ResourceNotFoundException;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dao.UserSessionRepository;
import com.prakass.aps.dto.*;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.UserSessionsEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final JwtTokenService jwtTokenService;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;

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
                        .email(userDetails.getUsername())
                        .build();

        UserSessionsEntity userSessionFromDB = userSessionRepository.save(userSessionsEntity);

        String accessToken =
                jwtTokenService.generateAccessToken(userDetails.getUsername(), roles, userSessionFromDB.getAccessTokenGuid(), userSessionFromDB.getRefreshTokenGuid());

        String refreshToken =
                jwtTokenService.generateRefreshToken(userDetails.getUsername(), roles, userSessionFromDB.getAccessTokenGuid(), userSessionFromDB.getRefreshTokenGuid());

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse generateRefreshToken(RefreshTokenPayload payload) {
        UserTokenDetails userTokenDetails = jwtTokenService.userTokenDetails(payload.refreshToken());

        UserSessionsEntity userSessionsEntity = userSessionRepository.findUserSessionsEntitiesByAccessTokenGuidAndRefreshTokenGuid(userTokenDetails.accessTokenGuid(), userTokenDetails.refreshTokenGuid())
                .orElseThrow( () -> new AuthException("Could not find user session for refresh token.")) ;

        String accessToken = jwtTokenService.generateAccessToken(userTokenDetails.userName(), userTokenDetails.roles(), userSessionsEntity.getAccessTokenGuid(), userSessionsEntity.getRefreshTokenGuid());

        String refreshToken = jwtTokenService.generateAccessToken(userTokenDetails.userName(), userTokenDetails.roles(), userSessionsEntity.getAccessTokenGuid(), userSessionsEntity.getRefreshTokenGuid());

        userSessionsEntity.setAccessTokenGuid(userTokenDetails.accessTokenGuid());
        userSessionsEntity.setRefreshTokenGuid(userTokenDetails.refreshTokenGuid());

        userSessionRepository.save(userSessionsEntity);

        return new LoginResponse(accessToken, refreshToken);
    }

    public String requestPasswordReset(SendEmailPayload payload) {
        UserAccountEntity userAccount = userAccountRepository.findFirstByEmail(payload.email());

        String passwordResetToken = jwtTokenService.generatePasswordResetToken(payload.email());

        //todo send email
        return passwordResetToken;
    }

    @Transactional
    public void resetPassword(PasswordRequestPayload payload) {
        if(!payload.newPassword().equals(payload.confirmPassword())){
            throw new AuthException("Password does not contain confirm password.");
        }

        UserPasswordDetails userPasswordDetails = jwtTokenService.verifyResetPasswordToken(payload.token());
        if(!TokenType.RESET_PASSWORD.getType().equals(userPasswordDetails.passwordType())){
            throw new AuthException("Invalid token.");
        }

        UserAccountEntity userAccount = userAccountRepository.findFirstByEmail(userPasswordDetails.username());
        if (userAccount == null) {
            throw new ResourceNotFoundException("Could not find user account.");
        }

        userAccount.setPasswordHash(passwordEncoder.encode(payload.newPassword()));
        userAccountRepository.save(userAccount);
    }
}
