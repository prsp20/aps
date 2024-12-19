package com.prakass.aps.controller;

import com.prakass.aps.dto.SignUpResponsePayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<SignUpResponsePayload> signup(
      @Valid @RequestBody UserSignupPayload userSignupPayload) {
    SignUpResponsePayload signUpResponsePayload = authService.signUpUser(userSignupPayload);
    return new ResponseEntity<>(signUpResponsePayload, HttpStatus.CREATED);
  }
}
