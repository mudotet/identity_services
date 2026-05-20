package com.example.identityservice.dto.response;

import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
  String roleName;
  String description;
  Set<PermissionResponse> permissions;
}
