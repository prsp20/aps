package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.group.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {}
