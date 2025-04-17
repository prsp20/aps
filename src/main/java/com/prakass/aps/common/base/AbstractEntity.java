package com.prakass.aps.common.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.mapstruct.ap.shaded.freemarker.template.utility.DateUtil;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    this.createdAt = ZonedDateTime.now(ZoneId.of("UTC")) ;
    this.updatedAt = ZonedDateTime.now(ZoneId.of("UTC")) ;
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = ZonedDateTime.now(ZoneId.of("UTC")) ;
  }
}
