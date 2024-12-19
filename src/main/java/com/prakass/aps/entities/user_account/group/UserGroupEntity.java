package com.prakass.aps.entities.user_account.group;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String guid;

  @ManyToOne
  @JoinColumn(name = "user_account_id")
  private UserAccountEntity userAccountEntity;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private GroupEntity groupEntity;
}
