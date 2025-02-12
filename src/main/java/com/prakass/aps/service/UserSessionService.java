package com.prakass.aps.service;

import com.prakass.aps.dao.UserSessionRepository;
import com.prakass.aps.dto.SessionJwtToken;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.security.SessionJwtTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserSessionService {
  private UserSessionRepository userSessionRepository;
  private SessionJwtTokenUtil sessionJwtTokenUtil;

  public String createAndSaveUserSession(
      UserDetails userDetails, UserAccountEntity userAccountEntity) {
    SessionJwtToken sessionEntity =
        sessionJwtTokenUtil.createSessionEntity(userDetails, userAccountEntity);

    userSessionRepository.save(sessionEntity.userSessionsEntity());

    return sessionEntity.jwtToken();
  }
}
