package com.example.identityservice.controller;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.RoleRequest;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.service.RoleService;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
  RoleService roleService;

  @PostMapping
  ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
    log.info(
        "INCOMING RoleRequest: roleName='{}', description='{}', permissions={}",
        request.getRoleName(),
        request.getDescription(),
        request.getPermissions());

    RoleResponse created = roleService.create(request);

    log.info(
        "OUTGOING RoleResponse: roleName='{}', description='{}', permissions={}",
        created.getRoleName(),
        created.getDescription(),
        created.getPermissions());
    return ApiResponse.<RoleResponse>builder()
        .code(1000)
        .result(roleService.create(request))
        .build();
  }

  @GetMapping
  ApiResponse<List<RoleResponse>> getAll() {
    return ApiResponse.<List<RoleResponse>>builder()
        .code(1000)
        .result(roleService.getAll())
        .build();
  }

  @DeleteMapping("/{role}")
  ApiResponse<Void> delete(@PathVariable String role) {
    roleService.delete(role);
    return ApiResponse.<Void>builder().code(1000).build();
  }
}
