package com.example.identityservice.controller;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  UserService userService;

  @PostMapping
  public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest userCreationRequest) {
    ApiResponse<User> response = new ApiResponse<>();
    response.setResult(userService.createUser(userCreationRequest));
    return response;
  }

  @GetMapping
  public List<User> getAllUsers() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("UserName: {}", authentication.getName());
    authentication
        .getAuthorities()
        .forEach(authority -> log.info("Authority: {}", authority.getAuthority()));

    return userService.getAllUsers();
  }

  @GetMapping("/{userId}")
  public UserResponse findUserById(@PathVariable String userId) {
    return userService.findUserById(userId);
  }

  @GetMapping("/my-info")
  public ApiResponse<UserResponse> getMyInfo() {
    return ApiResponse.<UserResponse>builder().result(userService.getMyInfo()).build();
  }

  @PutMapping("/{userId}")
  public UserResponse updateUser(
      @PathVariable String userId, @RequestBody UserUpdateRequest userUpdateRequest) {
    return userService.updateUser(userId, userUpdateRequest);
  }

  @DeleteMapping("/{userId}")
  public String deleteUser(@PathVariable String userId) {
    return userService.deleteUser(userId);
  }
}
