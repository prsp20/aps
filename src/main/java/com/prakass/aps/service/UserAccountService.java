package com.prakass.aps.service;

import com.prakass.aps.dao.UserAccountRepository;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {
  @Autowired private UserAccountRepository userAccountRepository;

  public UserAccountEntity getUserAccountWithRoles(Long id) {
    UserAccountEntity userAccount = userAccountRepository.findById(id).get();
    userAccount.setRoles(userAccountRepository.findAllRolesByUserId(id));
    return userAccount;
  }
}
