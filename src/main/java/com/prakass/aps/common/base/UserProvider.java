package com.prakass.aps.common.base;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserProvider {

    public String getUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = null;
        if(principal instanceof UserDetails) {
             userName = ((UserDetails) principal).getUsername();
        }
        return userName;
    }

    public List<String> getUserRole(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> roles = new ArrayList<>();
        if(principal instanceof UserDetails) {
            roles = ((UserDetails) principal).getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        }
        return roles;
    }
}
