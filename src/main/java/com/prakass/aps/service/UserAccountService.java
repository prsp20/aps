package com.prakass.aps.service;

import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAccountService {
  private UserAccountRepository userAccountRepository;

  public UserAccountEntity getUserAccountWithRoles(Long id) {
    UserAccountEntity userAccount = userAccountRepository.findById(id).get();
    userAccount.setRoles(userAccountRepository.findAllRolesByUserId(id));
    return userAccount;
  }
}
