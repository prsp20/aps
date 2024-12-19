package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.group.UserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroupEntity, Long> {}
