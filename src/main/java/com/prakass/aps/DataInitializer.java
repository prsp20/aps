package com.prakass.aps;

import com.prakass.aps.dao.*;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.entities.user_account.group.GroupEntity;
import com.prakass.aps.entities.user_account.group.UserGroupEntity;
import com.prakass.aps.entities.user_account.role.GroupRoleEntity;
import com.prakass.aps.entities.user_account.role.UserRole;
import com.prakass.aps.entities.user_account.role.UserRoleEntity;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private UserGroupRepository userGroupRepository;
  @Autowired private GroupRoleRepository groupRoleRepository;
  @Autowired private UserRoleRepository userRoleRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @PostConstruct
  public void init() {
    if (userAccountRepository.findFirstByEmail("john.doe@example.com") != null) {
      return;
    }
    UserAccountEntity user = initializeUserAccountEntity();
    GroupEntity groupEntity = initializeGroupEntity();
    GroupRoleEntity groupRoleEntity1 = initializeGroupRoleEntity(groupEntity, UserRole.ADMIN);
    GroupRoleEntity groupRoleEntity2 = initializeGroupRoleEntity(groupEntity, UserRole.USER);

    UserGroupEntity userGroupEntity = initializeUserGroupEntity(user, groupEntity);

    UserRoleEntity userRoleEntity = initializeUserRoleEntity(user, UserRole.SUPERADMIN);

    userAccountRepository.save(user);
    groupRepository.save(groupEntity);
    userRoleRepository.save(userRoleEntity);
    userGroupRepository.save(userGroupEntity);

    groupRoleRepository.save(groupRoleEntity1);
    groupRoleRepository.save(groupRoleEntity2);
  }

  public UserAccountEntity initializeUserAccountEntity() {
    UserAccountEntity userAccount = new UserAccountEntity();
    userAccount.setGuid(UUID.randomUUID().toString());
    userAccount.setFirstName("John");
    userAccount.setLastName("Doe");
    userAccount.setEmail("dev.dev@example.com");
    userAccount.setUsername("johndoe");
    userAccount.setPasswordHash(passwordEncoder.encode("dev1"));
    userAccount.setCountryCode("US");
    userAccount.setPhoneNumber("1234567890");
    userAccount.setEmailVerified(true);
    userAccount.setPhoneVerified(false);
    userAccount.setAccountStatus("ACTIVE");
    userAccount.setMfaEnabled(true);
    userAccount.setFailedLoginAttempts(0);
    userAccount.setLastLogin(LocalDateTime.now().minusDays(1));
    userAccount.setLastPasswordChanged(LocalDateTime.now().minusMonths(1));
    userAccount.setPasswordResetToken(null);
    userAccount.setPasswordResetTokenGenerated(null);
    userAccount.setAccountLockedUntil(null);
    userAccount.setSecurityQuestion("What is your pet's name?");
    userAccount.setSecurityQuestionHash("hashedanswer");
    userAccount.setCreatedAt(LocalDateTime.now().minusYears(1));
    userAccount.setUpdatedAt(LocalDateTime.now());
    userAccount.setDeletedAt(null);
    return userAccount;
  }

  public GroupEntity initializeGroupEntity() {
    GroupEntity group = new GroupEntity();
    group.setGuid(UUID.randomUUID().toString());
    group.setName("Admin Group");
    group.setDescription("Group for administrative users");
    return group;
  }

  public GroupRoleEntity initializeGroupRoleEntity(GroupEntity group, UserRole role) {
    GroupRoleEntity groupRole = new GroupRoleEntity();
    groupRole.setGuid(UUID.randomUUID().toString());
    groupRole.setGroupEntity(group);
    groupRole.setRole(role);
    return groupRole;
  }

  public UserGroupEntity initializeUserGroupEntity(
      UserAccountEntity userAccount, GroupEntity group) {
    UserGroupEntity userGroup = new UserGroupEntity();
    userGroup.setGuid(UUID.randomUUID().toString());
    userGroup.setUserAccountEntity(userAccount);
    userGroup.setGroupEntity(group);
    return userGroup;
  }

  public UserRoleEntity initializeUserRoleEntity(UserAccountEntity userAccount, UserRole role) {
    UserRoleEntity userRole = new UserRoleEntity();
    userRole.setGuid(UUID.randomUUID().toString());
    userRole.setUserAccountEntity(userAccount);
    userRole.setRole(role);
    return userRole;
  }
}
