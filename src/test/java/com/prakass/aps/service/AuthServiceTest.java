package com.prakass.aps.service;

import com.prakass.aps.common.exception.DuplicateEmailException;
import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.mapper.UserAccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceTest {
    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountMapper userAccountMapper;

    private AuthService authService;
    private UserSignupPayload userSignupPayload;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(
                userAccountRepository,
                passwordEncoder,
                userAccountMapper
        );
        userSignupPayload = new UserSignupPayload();
        userSignupPayload.setEmail("unique-mail@email.com");
        userSignupPayload.setPassword("password");
        userSignupPayload.setFirstName("firstName");
        userSignupPayload.setLastName("lastName");
    }

    @Test
    @Transactional
    public void signUp_should_callSaveMethodForSignUpWithUniqueEmail(){
        when(userAccountRepository.existsByEmail(userSignupPayload.getEmail())).thenReturn(Boolean.FALSE);

        authService.signUpUser(userSignupPayload);
        verify(userAccountRepository).save(any(UserAccountEntity.class));
    }

    @Test
    @Transactional
    public void signUp_should_throwDuplicateEmailExceptionForSignUpWithExistingEmail(){
        when(userAccountRepository.existsByEmail(userSignupPayload.getEmail())).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> authService.signUpUser(userSignupPayload))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email Already Exists");
    }

    @Test
    @Transactional
    public void signUp_should_callPasswordEncoderForSignUpWithUniqueEmail(){
        when(userAccountRepository.existsByEmail(userSignupPayload.getEmail())).thenReturn(Boolean.FALSE);

        authService.signUpUser(userSignupPayload);
        verify(passwordEncoder).encode(userSignupPayload.getPassword());
    }


}