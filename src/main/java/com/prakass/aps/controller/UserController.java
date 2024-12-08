package com.prakass.aps.controller;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/{userId}")
    public UserAccountEntity getUser(@PathVariable("userId") Long userId) {
        return userAccountService.getUserAccountWithRoles(userId);
    }
}
