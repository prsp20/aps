package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.UserSessionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSessionsEntity, Long> {}
