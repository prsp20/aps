package com.prakass.aps.entities.user_account.role;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guid;

    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccountEntity userAccountEntity;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
