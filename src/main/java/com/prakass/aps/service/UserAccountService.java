package com.prakass.aps.service;

import com.prakass.aps.common.exception.ResourceNotFoundException;
import com.prakass.aps.dao.AccessTokenRepository;
import com.prakass.aps.dao.RefreshTokenRepository;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dto.LoginResponse;
import com.prakass.aps.entities.token.AccessToken;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.utils.JwtToken;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserAccountService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;
    private UserAccountRepository userAccountRepository;
    private final TokenService tokenService;
    private JwtToken jwtToken;

    public UserAccountEntity getUserAccountWithRoles(Long id) {
        UserAccountEntity userAccount = userAccountRepository.findById(id).get();
        userAccount.setRoles(userAccountRepository.findAllRolesByUserId(id));
        return userAccount;
    }

    public LoginResponse getUserLoginResponse(UserDetails userDetails) {
        UserAccountEntity userAccountEntity = userAccountRepository.findFirstByEmail(userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String accessToken = jwtToken.generateToken(userDetails.getUsername(), roles, "accessToken");
        String aToken = tokenService.createAccessToken(accessToken, userAccountEntity);
        String refreshToken = jwtToken.generateToken(userDetails.getUsername(), roles, "refreshToken");
        String rToken = tokenService.createRefreshToken(refreshToken, userAccountEntity);
        return new LoginResponse(aToken, rToken);
    }

    public LoginResponse generateRefreshToken(String accessToken) {
        updateAccessToken(accessToken);


        return null;
    }


    private void updateAccessToken(String token) {
        AccessToken accessToken = accessTokenRepository.findAccessTokenByAccessToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid access token", HttpStatus.BAD_REQUEST));
        accessToken.setExpired(true);
        accessTokenRepository.save(accessToken);
    }
}
