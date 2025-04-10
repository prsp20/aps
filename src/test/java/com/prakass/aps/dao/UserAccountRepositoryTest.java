package com.prakass.aps.dao;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.group.GroupEntity;
import com.prakass.aps.entities.user_account.group.UserGroupEntity;
import com.prakass.aps.entities.user_account.role.GroupRoleEntity;
import com.prakass.aps.entities.user_account.role.UserRole;
import com.prakass.aps.entities.user_account.role.UserRoleEntity;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserAccountRepositoryTest {
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private UserRoleRepository userRoleRepository;
  @Autowired private UserGroupRepository userGroupRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private GroupRoleRepository groupRoleRepository;

  @Test
  public void userExistsShouldReturnTrueWhenUserExists() {
    UserAccountEntity userAccountEntity = new UserAccountEntity();
    userAccountEntity.setEmail("john-doe@gmail.com");
    userAccountRepository.save(userAccountEntity);

    boolean userExists = userAccountRepository.existsByEmail("john-doe@gmail.com");
    Assertions.assertThat(userExists).isTrue();
  }

  @Test
  public void userExistsShouldReturnFalseWhenUserDoesNotExists() {
    boolean userExists = userAccountRepository.existsByEmail("random-email@gmail.com");
    Assertions.assertThat(userExists).isFalse();
  }

  @Test
  public void findAllRolesByUserIdShouldReturnAllRolesCorrectly() {
    GroupRoleEntity groupRoleEntityAdmin = new GroupRoleEntity();
    groupRoleEntityAdmin.setRole(UserRole.ADMIN);
    groupRoleRepository.save(groupRoleEntityAdmin);

    GroupRoleEntity groupRoleEntityUser = new GroupRoleEntity();
    groupRoleEntityUser.setRole(UserRole.USER);
    groupRoleRepository.save(groupRoleEntityUser);

    GroupEntity groupEntity = new GroupEntity();
    groupEntity.setName("group");
    groupEntity.setGroupRoleEntities(List.of(groupRoleEntityUser, groupRoleEntityAdmin));
    groupRepository.save(groupEntity);

    UserAccountEntity userAccount = new UserAccountEntity();
    userAccount.setEmail("john.doe@example.com");
    userAccount = userAccountRepository.save(userAccount);

    UserRoleEntity userRole = new UserRoleEntity();
    userRole.setUserAccountEntity(userAccount);
    userRole.setRole(UserRole.SUPERADMIN);
    userRoleRepository.save(userRole);

    UserGroupEntity userGroup = new UserGroupEntity();
    userGroup.setUserAccountEntity(userAccount);
    userGroup.setGroupEntity(groupEntity);
    userGroupRepository.save(userGroup);

    Set<String> roles = userAccountRepository.findAllRolesByUserId(userAccount.getId());

    Assertions.assertThat(roles).containsExactlyInAnyOrder("SUPERADMIN", "ADMIN", "USER");
  }

  @Test
  public void findAllRolesByUserIdShouldReturnEmptySetWhenNoRoleIsAssigned() {
    UserAccountEntity userAccount = new UserAccountEntity();
    userAccount.setEmail("john-doe-2@example.com");
    userAccount = userAccountRepository.save(userAccount);

    Set<String> roles = userAccountRepository.findAllRolesByUserId(userAccount.getId());

    Assertions.assertThat(roles).isEmpty();
  }

  @Test
  public void findAllRolesByUserIdShouldReturnGroupRolesCorrectly() {
    GroupRoleEntity groupRoleEntityUser = new GroupRoleEntity();
    groupRoleEntityUser.setRole(UserRole.USER);
    groupRoleRepository.save(groupRoleEntityUser);

    GroupEntity groupEntity = new GroupEntity();
    groupEntity.setName("group");
    groupEntity.setGroupRoleEntities(List.of(groupRoleEntityUser));
    groupRepository.save(groupEntity);

    UserAccountEntity userAccount = new UserAccountEntity();
    userAccount.setEmail("john-doe-3@example.com");
    userAccount = userAccountRepository.save(userAccount);

    UserGroupEntity userGroup = new UserGroupEntity();
    userGroup.setUserAccountEntity(userAccount);
    userGroup.setGroupEntity(groupEntity);
    userGroupRepository.save(userGroup);

    Set<String> roles = userAccountRepository.findAllRolesByUserId(userAccount.getId());

    Assertions.assertThat(roles).containsExactly("USER");
  }

  @Test
  public void findAllRolesByUserIdShouldReturnUserRolesCorrectly() {
    UserAccountEntity userAccount = new UserAccountEntity();
    userAccount.setEmail("john-doe-4@example.com");
    userAccount = userAccountRepository.save(userAccount);

    UserRoleEntity userRole = new UserRoleEntity();
    userRole.setUserAccountEntity(userAccount);
    userRole.setRole(UserRole.SUPERADMIN);
    userRoleRepository.save(userRole);

    Set<String> roles = userAccountRepository.findAllRolesByUserId(userAccount.getId());

    Assertions.assertThat(roles).containsExactly("SUPERADMIN");
  }
}
