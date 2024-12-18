package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {
    UserAccountEntity findFirstByEmail(String email);

    @Query(value = """
            SELECT ur.role FROM user_role ur WHERE ur.user_account_id = :id
            UNION
            SELECT gr.role FROM group_role gr
            JOIN user_group ug ON gr.group_id = ug.group_id
            WHERE ug.user_account_id = :id
            """, nativeQuery = true)
    Set<String> findAllRolesByUserId(@Param("id") Long id);

    boolean existsByEmail(String email);
}
