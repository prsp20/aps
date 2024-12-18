package com.prakass.aps.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthPayload {
  private String Guid;
  private String email;
  private String firstName;
  private String lastName;
  private LocalDateTime createdAt;
}
