package com.prakass.aps.common.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prakass.aps.utils.DateUtils;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

@Getter
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity {

  @Setter(AccessLevel.PROTECTED)
  @JsonIgnore
  private ZonedDateTime createdAt;

  @Setter(AccessLevel.PROTECTED)
  @JsonIgnore
  private ZonedDateTime updatedAt;

  @PrePersist
  public void onPersist() {
    this.createdAt = DateUtils.getZonedDateTime();
    this.updatedAt = DateUtils.getZonedDateTime();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = DateUtils.getZonedDateTime();
  }
}
