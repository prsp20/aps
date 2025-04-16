package com.prakass.aps.entities.token;

import com.prakass.aps.common.base.AbstractEntity;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "access_token")
public class AccessToken extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", columnDefinition = "text", length = 1000, nullable = false)
    private String token;

    @Column(name = "expired" )
    private boolean expired;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccountEntity userAccountEntity;


}
