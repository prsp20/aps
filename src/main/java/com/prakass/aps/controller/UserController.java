package com.prakass.aps.controller;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.service.UserAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private UserAccountService userAccountService;

  @GetMapping("/{userId}")
  public UserAccountEntity getUser(@PathVariable("userId") Long userId) {
    return userAccountService.getUserAccountWithRoles(userId);
  }
}
