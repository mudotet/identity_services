package com.example.identityservice.dto.response;

import java.time.LocalDate;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
  String id;
  String userName;
  String passWord;
  String firstName;
  String lastName;
  LocalDate dob;

  Set<RoleResponse> roles;
}
