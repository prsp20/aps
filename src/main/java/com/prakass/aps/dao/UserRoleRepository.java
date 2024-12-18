package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.role.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {}
