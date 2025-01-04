package com.prakass.aps.service;

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
@AllArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

  UserAccountRepository userAccountRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserAccountEntity userAccountEntity = userAccountRepository.findFirstByEmail(email);
    if (userAccountEntity == null) {
      return null;
    }
    userAccountEntity.setRoles(userAccountRepository.findAllRolesByUserId(userAccountEntity.getId()));

    return User.builder()
        .username(userAccountEntity.getUsername())
        .password(userAccountEntity.getPasswordHash())
        .authorities(userAccountEntity.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
        .build();
  }
}
