package com.example.identityservice.dto.request;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
  String passWord;
  String firstName;
  String lastName;
  LocalDate dob;

  List<String> roles;
}
