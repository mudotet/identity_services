package com.example.identityservice.controller;

import com.example.identityservice.dto.request.*;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
  AuthenticationService authenticationService;

  @PostMapping("/token")
  public ApiResponse<AuthenticationResponse> authenticateUser(
      @RequestBody AuthenticationRequest authenticationRequest) {
    AuthenticationResponse authenticationResponse =
        authenticationService.authenticateUser(authenticationRequest);
    return ApiResponse.<AuthenticationResponse>builder().result(authenticationResponse).build();
  }

  @PostMapping("/introspect")
  public ApiResponse<IntrospectResponse> introspectUser(
      @RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
    IntrospectResponse introspectResponse = authenticationService.introspectUser(introspectRequest);
    return ApiResponse.<IntrospectResponse>builder().result(introspectResponse).build();
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest)
      throws ParseException, JOSEException {
    authenticationService.logout(logoutRequest);
    return ApiResponse.<Void>builder().code(200).build();
  }

  @PostMapping("/refresh")
  public ApiResponse<AuthenticationResponse> refreshToken(
      @RequestBody RefreshTokenRequest refreshTokenRequest) throws ParseException, JOSEException {
    return ApiResponse.<AuthenticationResponse>builder()
        .code(100)
        .result(authenticationService.refreshToken(refreshTokenRequest))
        .build();
  }
}
