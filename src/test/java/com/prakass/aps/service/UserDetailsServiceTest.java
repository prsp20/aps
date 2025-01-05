package com.prakass.aps.service;

import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.security.AuthUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserDetailsServiceTest {
  @Mock private UserDetailsService userDetailsService;

  @Mock private UserAccountRepository userAccountRepository;

  private UserAccountEntity userAccount;
  private Set<String> userRoles;

  @BeforeEach
  public void setup() {
    userDetailsService = new AuthUserDetailsService(userAccountRepository);
    userAccount =
        UserAccountEntity.builder()
            .id(1L)
            .email("john-doe@gmail.com")
            .username("john-doe")
            .passwordHash("hashedPassword")
            .build();

    userRoles = new HashSet<String>();
    userRoles.add("USER");
  }

  @Test
  public void loadUserByUsernameShouldCorrectUserDetails() {
    when(userAccountRepository.findFirstByEmail(userAccount.getEmail())).thenReturn(userAccount);

    UserDetails userDetails = userDetailsService.loadUserByUsername(userAccount.getEmail());

    assertNotNull(userDetails);
    assertEquals(userDetails.getUsername(), userAccount.getUsername());
    assertEquals(userDetails.getPassword(), userAccount.getPasswordHash());
  }

  @Test
  public void loadUserByUsernameShouldReturnCorrectUserRole() {
    when(userAccountRepository.findFirstByEmail(userAccount.getEmail())).thenReturn(userAccount);
    when(userAccountRepository.findAllRolesByUserId(userAccount.getId())).thenReturn(userRoles);

    UserDetails userDetails = userDetailsService.loadUserByUsername(userAccount.getEmail());

    assertTrue(userDetails.getAuthorities().stream().anyMatch(
            grantedAuthority -> grantedAuthority.getAuthority().equals("USER")));
  }

  @Test
  public void loadUserByEmailShouldReturnNullWhenUserDoesNotExist() {
    when(userAccountRepository.findFirstByEmail(userAccount.getEmail())).thenReturn(userAccount);

    assertNull(userDetailsService.loadUserByUsername("random@mail.com"));
  }
}
