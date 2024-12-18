package com.prakass.aps.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class AuthPayload {
    private String Guid;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
}
