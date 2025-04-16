package com.prakass.aps.security;

import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AuthUserDetailsService implements UserDetailsService {

  private final UserAccountRepository userAccountRepository;

  public AuthUserDetailsService(UserAccountRepository userAccountRepository) {
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserAccountEntity userAccountEntity = userAccountRepository.findFirstByEmail(email);
    if (userAccountEntity == null) {
      return null;
    }
    userAccountEntity.setRoles(
        userAccountRepository.findAllRolesByUserId(userAccountEntity.getId()));

    return User.builder()
        .username(userAccountEntity.getEmail())
        .password(userAccountEntity.getPasswordHash())
        .authorities(
            userAccountEntity.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList())
        .build();
  }
}
