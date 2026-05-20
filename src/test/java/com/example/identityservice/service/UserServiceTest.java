package com.example.identityservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "/test.properties")
public class UserServiceTest {
  @Autowired UserService userService;

  @MockBean UserRepository userRepository;

  private UserCreationRequest userCreationRequest;
  private UserResponse userResponse;
  private User user;
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
    userResponse =
        UserResponse.builder()
            .id("iaocsadjksadsidjsa")
            .userName("test")
            .dob(dob)
            .firstName("test")
            .lastName("test")
            .build();
    // Tự build User
    user =
        User.builder()
            .passWord("12345678")
            .id("iaocsadjksadsidjsa")
            .userName("test")
            .dob(dob)
            .firstName("test")
            .lastName("test")
            .build();
  }

  @Test
  void createUser_validRequest_success() {
    // Given
    when(userRepository.existsByUserName(userCreationRequest.getUserName())).thenReturn(false);
    when(userRepository.save(any())).thenReturn(user);

    // When
    var result = userService.createUser(userCreationRequest);

    // Then
    Assertions.assertEquals(userResponse.getId(), "iaocsadjksadsidjsa");
    Assertions.assertEquals(userResponse.getUserName(), "test");
  }

  @Test
  void createUser_userExisted_fail() {
    // Given
    when(userRepository.existsByUserName(userCreationRequest.getUserName())).thenReturn(true);

    // When
    var exception =
        Assertions.assertThrows(
            AppException.class, () -> userService.createUser(userCreationRequest));

    // Then
    Assertions.assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
    Assertions.assertEquals("User already exists", exception.getMessage());
    Assertions.assertEquals(1001, exception.getErrorCode().getCode());
  }
}
