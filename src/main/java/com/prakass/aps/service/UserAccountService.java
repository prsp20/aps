package com.prakass.aps.service;

import com.prakass.aps.common.base.UserProvider;
import com.prakass.aps.common.exception.ResourceNotFoundException;
import com.prakass.aps.dao.AccessTokenRepository;
import com.prakass.aps.dao.RefreshTokenRepository;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dto.LoginResponse;
import com.prakass.aps.dto.RefreshTokenPayload;
import com.prakass.aps.entities.token.RefreshToken;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.utils.JwtToken;
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
    private UserProvider userProvider;

    public UserAccountEntity getUserAccountWithRoles(Long id) {
        UserAccountEntity userAccount = userAccountRepository.findById(id).get();
        userAccount.setRoles(userAccountRepository.findAllRolesByUserId(id));
        return userAccount;
    }

    public LoginResponse getUserLoginResponse(UserDetails userDetails) {
        UserAccountEntity userAccountEntity = userAccountRepository.findFirstByEmail(userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return getLoginResponseWithToken(userDetails.getUsername(), roles, userAccountEntity);
    }

    private LoginResponse getLoginResponseWithToken(String userName, List<String> roles, UserAccountEntity userAccountEntity) {
        String accessToken = jwtToken.generateToken(userName, roles, "accessToken");
        String aToken = tokenService.createAccessToken(accessToken, userAccountEntity);
        String refreshToken = jwtToken.generateToken(userName, roles, "refreshToken");
        String rToken = tokenService.createRefreshToken(refreshToken, userAccountEntity);
        return new LoginResponse(aToken, rToken);
    }

    public LoginResponse generateRefreshToken(RefreshTokenPayload refreshToken) {
        updateRefreshToken(refreshToken.refreshToken());
        String userName = jwtToken.extractUsername(refreshToken.refreshToken());
        UserAccountEntity userAccountEntity = userAccountRepository.findFirstByEmail(userName);
        List<String> role = userProvider.getUserRole();
        String userNameFromProvider = userProvider.getUserName();
        return getLoginResponseWithToken(userNameFromProvider, role, userAccountEntity);
    }


    private void updateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid access token", HttpStatus.BAD_REQUEST));
        refreshToken.setExpired(true);
        refreshTokenRepository.save(refreshToken);
    }
}
