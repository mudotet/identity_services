package com.example.identityservice.controller;

import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/test.properties")
public class UserControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  @MockBean private UserMapper userMapper;

  private UserCreationRequest userCreationRequest;
  private UserResponse userResponse;
  private LocalDate dob;

  @BeforeEach
  void initData() {
    dob = LocalDate.of(2004, 12, 9);

    userCreationRequest =
        UserCreationRequest.builder()
            .userName("test")
            .dob(dob)
            .firstName("test")
            .lastName("test")
            .passWord("12345678")
            .build();
    // Tự build UserResponse
    userResponse = UserResponse.builder().id("iaocsadjksadsidjsa").userName("test").build();
  }

  @Test
  void createUser_validRequest_success() throws Exception {
    // Given
    log.info("test");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(userCreationRequest);
    User user =
        User.builder()
            .id("some-id")
            .userName(userCreationRequest.getUserName())
            .passWord(userCreationRequest.getPassWord())
            .firstName(userCreationRequest.getFirstName())
            .lastName(userCreationRequest.getLastName())
            .dob(dob)
            .build();

    Mockito.when(userService.createUser(ArgumentMatchers.any(UserCreationRequest.class)))
        .thenReturn(user);

    // When, Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
        .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("some-id"));
  }

  @Test
  void createUser_userNameInvalid_fail() throws Exception {
    // Given
    log.info("test");
    userCreationRequest.setUserName("");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(userCreationRequest);
    User user =
        User.builder()
            .id("some-id")
            .userName(userCreationRequest.getUserName())
            .passWord(userCreationRequest.getPassWord())
            .firstName(userCreationRequest.getFirstName())
            .lastName(userCreationRequest.getLastName())
            .dob(dob)
            .build();

    Mockito.when(userService.createUser(ArgumentMatchers.any(UserCreationRequest.class)))
        .thenReturn(user);

    // When, Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers.jsonPath("message")
                .value("User name must be at least 3 characters long"));
  }
}
