package com.prakass.aps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakass.aps.config.SecurityConfig;
import com.prakass.aps.dto.AuthPayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.service.AuthService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthService authService;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void signUpShouldReturnIsCreatedStatus() throws Exception {
    UserSignupPayload userSignupPayload =
        UserSignupPayload.builder()
            .email("john-doe@mail.com")
            .password("password")
            .firstName("John")
            .lastName("Doe")
            .build();

    LocalDateTime now = LocalDateTime.of(2024, 12, 16, 23, 15, 20);

    AuthPayload authPayload =
        AuthPayload.builder()
            .email(userSignupPayload.getEmail())
            .firstName(userSignupPayload.getFirstName())
            .lastName(userSignupPayload.getLastName())
            .createdAt(now)
            .Guid("unique-guid")
            .build();

    when(authService.signUpUser(userSignupPayload)).thenReturn(authPayload);

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(authPayload.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(authPayload.getLastName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.guid").value(authPayload.getGuid()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(authPayload.getEmail()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.createdAt")
                .value(equalTo(authPayload.getCreatedAt().toString())));
  }

  @Test
  void signUpShouldRejectInvalidEmail() throws Exception {
    UserSignupPayload userSignupPayload =
        UserSignupPayload.builder()
            .email("john-doe.com")
            .password("password")
            .firstName("John")
            .lastName("Doe")
            .build();

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid email format")));
  }

  @Test
  void signUpShouldReturnValidationMessagesOnInvalidPayload() throws Exception {
    UserSignupPayload userSignupPayload = UserSignupPayload.builder().build();
    String[] expectedMessages = {
      "Password is required",
      "Email is required",
      "First name is required",
      "Last name is required",
    };

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.validation_errors[*].message")
                .value(Matchers.hasItems(expectedMessages)));
  }
}
