package com.example.identityservice.configuration;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {

    ErrorCode code = ErrorCode.UNAUTHORIZED;

    response.setStatus(code.getHttpCode().value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    ApiResponse<?> apiResponse =
        ApiResponse.builder().code(code.getCode()).message(code.getMessage()).build();

    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
  }
}
