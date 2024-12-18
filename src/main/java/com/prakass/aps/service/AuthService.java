package com.prakass.aps.service;

import com.prakass.aps.common.exception.DuplicateEmailException;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dto.AuthPayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.mapper.UserAccountMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class AuthService {
    private UserAccountRepository userAccountRepository;
    private PasswordEncoder passwordEncoder;
    private UserAccountMapper userAccountMapper;

    @Transactional
    public AuthPayload signUpUser(UserSignupPayload userSignupPayload) {
        if (userAccountRepository.existsByEmail(userSignupPayload.getEmail())){
            throw new DuplicateEmailException("Email Already Exists");
        }
        UserAccountEntity userAccount = userAccountMapper.userSignupPayloadToUserAccountEntity(userSignupPayload);
        userAccount.setPasswordHash(passwordEncoder.encode(userSignupPayload.getPassword()));
        userAccountRepository.save(userAccount);

        return userAccountMapper.userAccountEntityToAuthPayload(userAccount);
    }
}
