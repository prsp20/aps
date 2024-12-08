package com.prakass.aps.entities.user_account.role;

import com.prakass.aps.entities.user_account.group.GroupEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guid;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity groupEntity;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
