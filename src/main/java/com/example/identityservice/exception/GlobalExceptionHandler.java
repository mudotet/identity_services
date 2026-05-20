package com.example.identityservice.exception;

import com.example.identityservice.dto.request.ApiResponse;
import jakarta.validation.ConstraintViolation;
import java.nio.file.AccessDeniedException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  private static final String MIN_ATTRIBUTE = "min";
  private static final String MAX_ATTRIBUTE = "max";

  @ExceptionHandler(value = Exception.class)
  ResponseEntity<ApiResponse> handleException(RuntimeException e) {
    ApiResponse response = new ApiResponse();
    response.setCode(ErrorCode.UNCATEGORIZED_ERROR.getCode());
    response.setMessage(ErrorCode.UNCATEGORIZED_ERROR.getMessage());
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = RuntimeException.class)
  ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
    ApiResponse response = new ApiResponse();
    response.setCode(1001);
    response.setMessage(e.getMessage());
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = AppException.class)
  ResponseEntity<ApiResponse> handleAppException(AppException e) {
    ErrorCode errorCode = e.getErrorCode();
    ApiResponse response = new ApiResponse();
    response.setCode(errorCode.getCode());
    response.setMessage(errorCode.getMessage());
    return ResponseEntity.status(errorCode.getHttpCode()).body(response);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
    String enumKey = e.getFieldError().getDefaultMessage();
    ErrorCode errorCode = ErrorCode.WRONG_ENUM_KEY;
    Map<String, Object> attributes = null;
    try {
      errorCode = ErrorCode.valueOf(enumKey);
      var constraintViolation =
          e.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);

      attributes = constraintViolation.getConstraintDescriptor().getAttributes();
      log.info(attributes.toString());
    } catch (IllegalArgumentException ex) {
    }

    ApiResponse response = new ApiResponse();
    response.setCode(errorCode.getCode());
    response.setMessage(
        Objects.nonNull(attributes)
            ? mapAtrribute(errorCode.getMessage(), attributes)
            : errorCode.getMessage());
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  ResponseEntity<ApiResponse> handleAcessDeniedException(AccessDeniedException e) {
    ErrorCode code = ErrorCode.UNAUTHORIZED;
    return ResponseEntity.status(code.getHttpCode())
        .body(ApiResponse.builder().code(code.getCode()).message(code.getMessage()).build());
  }

  private String mapAtrribute(String message, Map<String, Object> attributes) {
    String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
    return message.replace("{min}", minValue);
  }
}
