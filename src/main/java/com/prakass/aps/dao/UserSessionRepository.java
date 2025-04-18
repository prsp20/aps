package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.UserSessionsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSessionsEntity, Long> {

  Optional<UserSessionsEntity> findUserSessionsEntitiesByRefreshTokenGuid(String refreshToken);
}
