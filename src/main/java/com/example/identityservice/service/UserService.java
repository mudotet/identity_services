package com.example.identityservice.service;

import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.Role;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.RoleRepository;
import com.example.identityservice.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import lombok.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
  UserRepository userRepository;
  UserMapper userMapper;
  RoleRepository roleRepository;
  PasswordEncoder passwordEncoder;

  public User createUser(UserCreationRequest userCreationRequest) {
    if (userRepository.existsByUserName(userCreationRequest.getUserName())) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    } else {
      User user = userMapper.toUser(userCreationRequest);
      user.setPassWord(passwordEncoder.encode(userCreationRequest.getPassWord()));

      HashSet<Role> roles = new HashSet<>();
      roles.add(Role.builder().roleName("USER").build());
      user.setRoles(roles);
      userRepository.save(user);
      return user;
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<User> getAllUsers() {
    log.info("In method getAllUsers");
    return userRepository.findAll();
  }

  @PostAuthorize("returnObject.userName == authentication.name")
  public UserResponse findUserById(String userId) {
    log.info("In method findUserById");
    return userMapper.toUserResponse(
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found")));
  }

  public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));
    userMapper.updateUser(user, userUpdateRequest);
    user.setPassWord(passwordEncoder.encode(userUpdateRequest.getPassWord()));
    var roles = roleRepository.findAllById(userUpdateRequest.getRoles());
    user.setRoles(new HashSet<>(roles));
    userRepository.save(user);
    return userMapper.toUserResponse(user);
  }

  public String deleteUser(String userId) {
    userRepository.deleteById(userId);
    return "Deleted user successfully";
  }

  @PostAuthorize("returnObject.userName == authentication.name")
  public UserResponse getMyInfo() {
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUserName(name);
    if (user == null) throw new AppException(ErrorCode.USER_NOT_FOUND);
    return userMapper.toUserResponse(user);
  }
}
