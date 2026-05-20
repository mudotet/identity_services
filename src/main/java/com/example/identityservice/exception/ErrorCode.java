package com.example.identityservice.exception;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
  USER_ALREADY_EXISTS(1001, "User already exists", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
  UNCATEGORIZED_ERROR(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
  USER_NOT_BLANK(1003, "User name cannot be blank", HttpStatus.BAD_REQUEST),
  PASSWORD_NOT_BLANK(1004, "Password cannot be blank", HttpStatus.BAD_REQUEST),
  FIRST_NAME_NOT_BLANK(1005, "First name cannot be blank", HttpStatus.BAD_REQUEST),
  LAST_NAME_NOT_BLANK(1006, "Last name cannot be blank", HttpStatus.BAD_REQUEST),
  DATE_OF_BIRTH_NOT_BLANK(1007, "Date of birth cannot be blank", HttpStatus.BAD_REQUEST),
  INVALID_LENGTH_OF_USER_NAME(
      1008, "User name must be at least {min} characters long", HttpStatus.BAD_REQUEST),
  INVALID_LENGTH_OF_PASSWORD(
      1008, "Password must be at least {min} characters long", HttpStatus.BAD_REQUEST),
  WRONG_ENUM_KEY(1009, "Wrong enum key, check your code", HttpStatus.BAD_REQUEST),
  INVALID_DOB(1010, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),
  UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED);
  private int code;
  private String message;
  private HttpStatusCode httpCode;

  ErrorCode(int code, String message, HttpStatusCode httpCode) {
    this.code = code;
    this.message = message;
    this.httpCode = httpCode;
  }
}
