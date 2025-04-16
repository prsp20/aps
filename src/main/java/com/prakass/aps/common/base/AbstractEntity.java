package com.prakass.aps.common.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity {

    @Setter(AccessLevel.PROTECTED)
    @JsonIgnore
    private LocalDateTime createdAt;

    @Setter(AccessLevel.PROTECTED)
    @JsonIgnore
    private LocalDateTime updatedAt;

    @PrePersist
    public void onPersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
