package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.role.GroupRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRoleRepository extends JpaRepository<GroupRoleEntity, Long> {

}