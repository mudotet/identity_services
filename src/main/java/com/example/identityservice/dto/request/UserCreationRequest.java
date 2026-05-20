package com.example.identityservice.dto.request;

import com.example.identityservice.validator.DobConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
  @NotBlank(message = "USER_NOT_BLANK")
  @Size(min = 3, message = "INVALID_LENGTH_OF_USER_NAME")
  String userName;

  @NotBlank(message = "PASSWORD_NOT_BLANK")
  @Size(min = 8, message = "INVALID_LENGTH_OF_PASSWORD")
  String passWord;

  @NotBlank(message = "FIRST_NAME_NOT_BLANK")
  String firstName;

  @NotBlank(message = "LAST_NAME_NOT_BLANK")
  String lastName;

  @DobConstraint(min = 18, message = "INVALID_DOB")
  LocalDate dob;
}
