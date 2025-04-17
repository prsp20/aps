package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.UserSessionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSessionsEntity, Long> {

    Optional<UserSessionsEntity> findUserSessionsEntitiesByRefreshTokenGuid(String refreshToken);
}
