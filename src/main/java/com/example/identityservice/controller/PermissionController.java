package com.example.identityservice.controller;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.service.PermissionService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
  PermissionService permissionService;

  @PostMapping
  ApiResponse<PermissionResponse> createPermission(
      @RequestBody PermissionRequest permissionRequest) {
    return ApiResponse.<PermissionResponse>builder()
        .code(100)
        .result(permissionService.create(permissionRequest))
        .build();
  }

  @GetMapping
  ApiResponse<List<PermissionResponse>> getAllPermissions() {
    return ApiResponse.<List<PermissionResponse>>builder()
        .code(100)
        .result(permissionService.getAll())
        .build();
  }

  @DeleteMapping("/{permission}")
  ApiResponse<Void> deletePermission(@PathVariable String permission) {
    permissionService.delete(permission);
    return ApiResponse.<Void>builder().build();
  }
}
