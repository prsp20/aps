package com.prakass.aps.entities.user_account.group;

import com.prakass.aps.entities.user_account.role.GroupRoleEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String guid;

    private String name;
    private String description;

    @OneToMany(mappedBy = "groupEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupRoleEntity> groupRoleEntities = new ArrayList<>();
}
