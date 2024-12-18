package com.prakass.aps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakass.aps.config.SecurityConfig;
import com.prakass.aps.dto.AuthPayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.service.AuthService;
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
  void SignUp_should_returnIsCreatedStatus() throws Exception {
    UserSignupPayload userSignupPayload = new UserSignupPayload();
    userSignupPayload.setEmail("john-doe@mail.com");
    userSignupPayload.setPassword("password");
    userSignupPayload.setFirstName("John");
    userSignupPayload.setLastName("Doe");

    LocalDateTime now = LocalDateTime.of(2024, 12, 16, 23, 15, 20);

    AuthPayload authPayload = new AuthPayload();
    authPayload.setEmail("john-doe@mail.com");
    authPayload.setGuid("unique-guid");
    authPayload.setCreatedAt(now);
    authPayload.setFirstName("John");
    authPayload.setLastName("Doe");

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
        .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt")
                .value(equalTo(authPayload.getCreatedAt().toString())));
  }

  @Test
  void SignUp_should_rejectMissingEmail() throws Exception {
    UserSignupPayload userSignupPayload = new UserSignupPayload();
    userSignupPayload.setPassword("password");
    userSignupPayload.setFirstName("John");
    userSignupPayload.setLastName("Doe");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Email is required")));
  }

  @Test
  void SignUp_should_rejectInvalidEmail() throws Exception {
    UserSignupPayload userSignupPayload = new UserSignupPayload();
    userSignupPayload.setEmail("john-doe.com");
    userSignupPayload.setPassword("password");
    userSignupPayload.setFirstName("John");
    userSignupPayload.setLastName("Doe");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid email format")));
  }

  @Test
  void SignUp_should_rejectMissingPassword() throws Exception {
    UserSignupPayload userSignupPayload = new UserSignupPayload();
    userSignupPayload.setEmail("john-doe@mail.com");
    userSignupPayload.setFirstName("John");
    userSignupPayload.setLastName("Doe");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Password is required")));
  }

  @Test
  void SignUp_should_rejectMissingFirstName() throws Exception {
    UserSignupPayload userSignupPayload = new UserSignupPayload();
    userSignupPayload.setEmail("john-doe@mail.com");
    userSignupPayload.setPassword("random-pass");
    userSignupPayload.setLastName("Doe");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("First name is required")));
  }

  @Test
  void SignUp_should_rejectMissingLastName() throws Exception {
    UserSignupPayload userSignupPayload = new UserSignupPayload();
    userSignupPayload.setEmail("john-doe@mail.com");
    userSignupPayload.setPassword("random-pass");
    userSignupPayload.setFirstName("John");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Last name is required")));
  }
}
